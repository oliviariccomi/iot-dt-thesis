import glob
import fileinput as fi
from biosppy import storage
import os
import matplotlib.pyplot as plt
import pandas as pd
import numpy as np
from statistics import mean
from biosppy.signals import ecg
import neurokit2 as nk
from sklearn.linear_model import LinearRegression
from pathlib import Path
from hrvanalysis import remove_outliers, remove_ectopic_beats, interpolate_nan_values, get_frequency_domain_features, get_time_domain_features



from pandas import DataFrame

pd.set_option('display.max_columns', None)  # or 1000
pd.set_option('display.max_rows', None)  # or 1000
pd.set_option('display.max_colwidth', None)

'''d = {'ID': [], 'Mean RR (ms)': [], 'STD RR/SDNN (ms)': [], 'Mean HR (Kubios\' style) (beats/min)': [],
     'Mean HR (beats/min)': [], 'STD HR (beats/min)': [], 'Min HR (beats/min)': [],
     'Max HR (beats/min)': [], 'RMSSD (ms)': [], 'NNxx': [], 'pNNxx (%)': [], 'HRV_LF': [], 'HRV_HF': [],
     'HRV_LFHF': [],
     'A0': [], 'A1': [], 'A2': [], 'A3': [], 'A4': [],
     'C0': [], 'C1': [], 'C2': [], 'C3': [], 'C4': [],
     'STRESS': []}'''

d = {'ID': [], 'mean_nni': [], 'sdnn': [], 'sdsd': [],
     'nni_50': [], 'pnni_50': [], 'rmssd': [],
     'mean_hr': [], 'max_hr': [], 'min_hr': [], 'std_hr': [], 'lf': [], 'hf': [],
     'lf_hf_ratio': [],
     'A0': [], 'A1': [], 'A2': [], 'A3': [], 'A4': [],
     'C0': [], 'C1': [], 'C2': [], 'C3': [], 'C4': [],
     'STRESS': []}

valori_basali = {}
t_k = ['mean_nni', 'sdnn', 'sdsd', 'nni_50', 'pnni_50', 'rmssd', 'mean_hr', 'max_hr', 'min_hr', 'std_hr']
f_k = ['lf', 'hf', 'lf_hf_ratio']
#####################
#    VARIABLES      #
#####################
fs = 100
step = 5  # in secondi
len_window = 20  # in secondi

hf = 9  # bassa
lf = 40  # alta
order = 8


#############################
#         FUNCTIONS         #
#############################
def rr_all (ecg_normal, fs):
    rpeaks = ecg.christov_segmenter2(ord=order, hf=hf, lf=lf, signal=ecg_normal, sampling_rate=fs)
    time = []
    for x in range(len(ecg_normal)):
        time.append(x * (1 / fs))
    x = np.asanyarray(time)
    ecg_values = []
    time_peaks = []
    for i in rpeaks:
        ecg_values.append(ecg_normal[i])
        time_peaks.append(x[i])
    rr = np.diff(time_peaks)
    rr = rr * 1000
    return rr

def timedomain(ecg_normal):
    rpeaks_normal = ecg.christov_segmenter2(ord=order, hf=hf, lf=lf, signal=ecg_normal, sampling_rate=fs)
    results = {}
    time = []
    for x in range(len(ecg_normal)):
        time.append(x * (1 / fs))
    x = np.asanyarray(time)

    ecg_values = []
    time_peaks = []
    for i in rpeaks_normal:
        ecg_values.append(ecg_normal[i])
        time_peaks.append(x[i])

    # Calculate results for the experiment - basal value
    rr = np.diff(time_peaks)
    rr = rr * 1000
    hr = 60000 / (rr)  # suppongo di avere rr in millisecondi quindi facendo 6000 / rr (ovvero i miei valori di rr

    results['Mean RR (ms)'] = np.mean(rr)
    results['STD RR/SDNN (ms)'] = np.std(rr)
    results['Mean HR (Kubios\' style) (beats/min)'] = 60000 / np.mean(rr)
    results['Mean HR (beats/min)'] = np.mean(hr)
    results['STD HR (beats/min)'] = np.std(hr)
    results['Min HR (beats/min)'] = np.min(hr)
    results['Max HR (beats/min)'] = np.max(hr)
    results['RMSSD (ms)'] = np.sqrt(np.mean(np.square(np.diff(rr))))
    results['NNxx'] = np.sum(np.abs(np.diff(rr)) > 50) * 1
    results['pNNxx (%)'] = 100 * np.sum((np.abs(np.diff(rr)) > 50) * 1) / len(rr)
    return results

def nn_intervals (ecg_normal):
    rr_intervals_list = rr_all(ecg_normal, fs)
    rr_intervals_without_outliers = remove_outliers(rr_intervals=rr_intervals_list,
                                                    low_rri=300, high_rri=2000)
    # This replace outliers nan values with linear interpolation
    interpolated_rr_intervals = interpolate_nan_values(rr_intervals=rr_intervals_without_outliers,
                                                       interpolation_method="linear")

    # This remove ectopic beats from signal
    nn_intervals_list = remove_ectopic_beats(rr_intervals=interpolated_rr_intervals, method="malik")
    # This replace ectopic beats nan values with linear interpolation
    interpolated_nn_intervals = interpolate_nan_values(rr_intervals=nn_intervals_list)

    return interpolated_nn_intervals


#############################
#        REST ECG           #
#############################
for file in glob.glob("../data/bitalino/ecg/normal/*.txt", ):
    name = Path(file).stem
    d['ID'].append(name)

    ecg_normal, mdata = storage.load_txt(file)
    ecg_basale = ecg_normal[:2999]

    # Calculate basal values
    if name not in valori_basali:
        valori_basali[name] = {}
        for k, v in get_time_domain_features(nn_intervals(ecg_basale)).items():
            if k in t_k:
                valori_basali[name].update({k: v})
        for ke, va in get_frequency_domain_features(nn_intervals(ecg_basale)).items():
            if ke in f_k:
                valori_basali[name].update({ke: va})

    # Calculate values
    for t, c in get_time_domain_features(nn_intervals(ecg_normal)).items():
        if t in t_k:
            basal_remove = valori_basali[name][t]
            new_val = c - basal_remove
            d[t].append(new_val)
    for te, ce in get_frequency_domain_features(nn_intervals(ecg_normal)).items():
        if te in f_k:
            b_r = valori_basali[name][te]
            n_v = ce - b_r
            d[te].append(n_v)

    d['STRESS'].append('NO')

    #############################
    #        REST EDA           #
    #############################
    with open("../data/bitalino/eda/normal/" + name + ".txt") as f:
        Y = pd.read_csv(f)
        Z = []
        for i in range(len(Y)):
            Z.append(i * (1 / fs))

        Y = Y.values
        X = pd.DataFrame(Z).values
        lenX = len(X)
        arrX = X[0: (len_window * fs - 1)]
        coef1 = []
        area1 = []
        for i in range(0, lenX - (len_window * fs - 1), step * fs):
            ll = LinearRegression()
            arrY = Y[i:i + (len_window * fs) - 1]

            ll.fit(arrX, arrY)
            # plt.plot(arrX, ll.coef_ * arrX + ll.intercept_, linewidth=1)
            arrY_test = ll.coef_ * arrX + ll.intercept_
            area = sum(abs(arrY_test - arrY))
            ## 1° feature: pendenza (ll.coef)
            coef1.append(ll.coef_.item())
            ## 2° feature: area
            area1.append(float(area))

        valori_basali[name].update({'coef': coef1[0]})
        valori_basali[name].update({'area': area1[0]})

        five_split_coef = np.array_split(coef1[1:], 5)
        for index, item in enumerate(five_split_coef):
            d['C' + str(index)].append(mean(item) - valori_basali[name]['coef'])

        five_split_area = np.array_split(area1[1:], 5)
        for index, item in enumerate(five_split_area):
            d['A' + str(index)].append(mean(item) - valori_basali[name]['area'])

# w = 0
#############################
#        STRESS ECG         #
#############################
for file in glob.glob("../data/bitalino/ecg/stressed/*.txt", ):
    name = Path(file).stem
    nn = name.split("_")[0]
    d['ID'].append(name)
    # w += 1
    ecg_normal, mdata = storage.load_txt(file)

    for a, b in get_time_domain_features(nn_intervals(ecg_normal)).items():
        if a in t_k:
            ba_re = valori_basali[nn][a]
            ne_va = b - ba_re
            d[a].append(ne_va)

    for aa, bb in get_frequency_domain_features(nn_intervals(ecg_normal)).items():
        if aa in f_k:
            br = valori_basali[nn][aa]
            nv = bb - br
            d[aa].append(nv)

    d['STRESS'].append('YES')

    #############################
    #        STRESS EDA         #
    #############################
    with open("../data/bitalino/eda/stressed/" + name + ".txt") as f:
        Y = pd.read_csv(f)
        Z = []
        for i in range(len(Y)):
            Z.append(i * (1 / fs))

        Y = Y.values
        X = pd.DataFrame(Z).values
        lenX = len(X)
        arrX = X[0: (len_window * fs - 1)]
        coef2 = []
        area2 = []
        for i in range(0, lenX - (len_window * fs - 1), step * fs):
            ll = LinearRegression()
            arrY = Y[i:i + (len_window * fs) - 1]

            ll.fit(arrX, arrY)
            # plt.plot(arrX, ll.coef_ * arrX + ll.intercept_, linewidth=1)
            arrY_test = ll.coef_ * arrX + ll.intercept_
            area = sum(abs(arrY_test - arrY))
            ## 1° feature: pendenza (ll.coef)
            coef2.append(float(ll.coef_.item()))
            ## 2° feature: area
            area2.append(float(area))

        five_split_coef_str = np.array_split(coef2, 5)
        for index, item in enumerate(five_split_coef_str):
            d['C' + str(index)].append(mean(item) - valori_basali[nn]['coef'])

        five_split_area_str = np.array_split(area2, 5)
        for index, item in enumerate(five_split_area_str):
            d['A' + str(index)].append(mean(item) - valori_basali[nn]['area'])

print(d)
df = pd.DataFrame(d)
df.to_csv(
    path_or_buf='../data/ml_metrics_datasets/datasets_fiveVal_valoriBasali/' + str(hf) + '_' + str(lf) + '_' + str(
        order) + '_vb.csv', index=False)

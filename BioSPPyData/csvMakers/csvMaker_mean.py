import glob
import fileinput as fi
from biosppy import storage
import os
import matplotlib.pyplot as plt
import pandas as pd
import numpy as np
from biosppy.signals import ecg
import neurokit2 as nk
from sklearn.linear_model import LinearRegression
from pathlib import Path

from pandas import DataFrame


pd.set_option('display.max_columns', None)  # or 1000
pd.set_option('display.max_rows', None)  # or 1000
pd.set_option('display.max_colwidth', None)

d = {'ID':[], 'Mean RR (ms)': [], 'STD RR/SDNN (ms)': [], 'Mean HR (Kubios\' style) (beats/min)': [],
     'Mean HR (beats/min)': [], 'STD HR (beats/min)': [], 'Min HR (beats/min)': [],
     'Max HR (beats/min)': [], 'RMSSD (ms)': [], 'NNxx': [], 'pNNxx (%)': [], 'HRV_LF': [], 'HRV_HF': [],
     'HRV_LFHF': [],
     'LL.COEF': [],
     #'A0':[],'A1':[],'A2':[],'A3':[],'A4':[],
     #'C0':[],'C1':[],'C2':[],'C3':[],'C4':[],
     'AREA': [],
     'STRESS': []}

#####################
#    VARIABLES      #
#####################
fs = 100
step = 5  # in secondi
len_window = 20  # in secondi


#############################
#         FUNCTIONS         #
#############################

def timedomain(ecg_normal):
    rpeaks_normal = ecg.christov_segmenter2(signal=ecg_normal, sampling_rate=fs)
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

#w = 0
#############################
#        REST ECG           #
#############################
for file in glob.glob("./data/bitalino/ecg/normal/*.txt", ):
    name = Path(file).stem
    d['ID'].append(name)
    #w += 1
    ecg_normal, mdata = storage.load_txt(file)
    if ecg_normal.size < 2:
        d['Mean RR (ms)'].append(np.nan)
        d['STD RR/SDNN (ms)'].append(np.nan)
        d['Mean HR (Kubios\' style) (beats/min)'].append(np.nan)
        d['Mean HR (beats/min)'].append(np.nan)
        d['STD HR (beats/min)'].append(np.nan)
        d['Min HR (beats/min)'].append(np.nan)
        d['Max HR (beats/min)'].append(np.nan)
        d['RMSSD (ms)'].append(np.nan)
        d['NNxx'].append(np.nan)
        d['pNNxx (%)'].append(np.nan)
        d['HRV_LF'].append(np.nan)
        d['HRV_HF'].append(np.nan)
        d['HRV_LFHF'].append(np.nan)
    else:
        rpeaks_normal = ecg.christov_segmenter2(signal=ecg_normal, sampling_rate=fs)
        for k, v in timedomain(ecg_normal).items():
            d[k].append(v)
        hrv_freq = nk.hrv_frequency(rpeaks_normal, sampling_rate=fs, show=True)
        d['HRV_LF'].append(hrv_freq['HRV_LF'].values[0])
        d['HRV_HF'].append(hrv_freq['HRV_HF'].values[0])
        d['HRV_LFHF'].append(hrv_freq['HRV_LFHF'].values[0])
    d['STRESS'].append('NO')


#############################
#        REST EDA           #
#############################
    with open("./data/eda/normal/"+name+".txt"):
        Y = pd.read_csv(file)
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

        '''c1 = coef1[::int(len(coef1) / 4.5)]
        for index, item in enumerate(c1):
            while (index < 5):'''
        d['LL.COEF'].append(float(np.mean(coef1)))

        '''a1 = area1[::int(len(area1) / 4.5)]
        for index, item in enumerate(a1):
            while (index < 5):'''
        d['AREA'].append(float(np.mean(area1)))



#w = 0
#############################
#        STRESS ECG         #
#############################
for file in glob.glob("./data/bitalino/ecg/stressed/*.txt", ):
    name = Path(file).stem
    d['ID'].append(name)
    #w += 1
    ecg_normal, mdata = storage.load_txt(file)
    if ecg_normal.size < 2:
        d['Mean RR (ms)'].append(np.nan)
        d['STD RR/SDNN (ms)'].append(np.nan)
        d['Mean HR (Kubios\' style) (beats/min)'].append(np.nan)
        d['Mean HR (beats/min)'].append(np.nan)
        d['STD HR (beats/min)'].append(np.nan)
        d['Min HR (beats/min)'].append(np.nan)
        d['Max HR (beats/min)'].append(np.nan)
        d['RMSSD (ms)'].append(np.nan)
        d['NNxx'].append(np.nan)
        d['pNNxx (%)'].append(np.nan)
        d['HRV_LF'].append(np.nan)
        d['HRV_HF'].append(np.nan)
        d['HRV_LFHF'].append(np.nan)

    else:
        rpeaks_normal = ecg.christov_segmenter2(signal=ecg_normal, sampling_rate=fs)
        for k, v in timedomain(ecg_normal).items():
            d[k].append(v)
        hrv_freq = nk.hrv_frequency(rpeaks_normal, sampling_rate=fs, show=True)
        d['HRV_LF'].append(hrv_freq['HRV_LF'].values[0])
        d['HRV_HF'].append(hrv_freq['HRV_HF'].values[0])
        d['HRV_LFHF'].append(hrv_freq['HRV_LFHF'].values[0])
    d['STRESS'].append('YES')

#############################
#        STRESS EDA         #
#############################
    with open("./data/eda/stressed/"+name+".txt"):
        Y = pd.read_csv(file)
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

        '''c2 = coef2[::int(len(coef2) / 4.5)]
        for index, item in enumerate(c2):
            while (index < 5):'''
        d['LL.COEF'].append(float(np.mean(coef2)))

        '''a2 = area2[::int(len(area2) / 4.5)]
        for index, item in enumerate(a2):
            while (index < 5):'''
        d['AREA'].append(float(np.mean(area2)))



df = pd.DataFrame(d)
df.to_csv(path_or_buf='../data/ml_metrics_datasets/prova_mean.csv', index=False)
print(df)

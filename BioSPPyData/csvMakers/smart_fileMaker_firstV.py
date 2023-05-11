import glob
from biosppy import storage
import pandas as pd
import numpy as np
from biosppy.signals import ecg
from sklearn.linear_model import LinearRegression
from pathlib import Path

from data.bitalino.ecg.scripts.ECG_functions import time_domain, frequency_domain

pd.set_option('display.max_columns', None)  # or 1000
pd.set_option('display.max_rows', None)  # or 1000
pd.set_option('display.max_colwidth', None)

d = {'ID': [], 'Mean RR (ms)': [], 'STD RR/SDNN (ms)': [], 'Mean HR (Kubios\' style) (beats/min)': [],
     'Mean HR (beats/min)': [], 'STD HR (beats/min)': [], 'Min HR (beats/min)': [],
     'Max HR (beats/min)': [], 'RMSSD (ms)': [], 'NNxx': [], 'pNNxx (%)': [], 'HRV_LF': [], 'HRV_HF': [],
     'HRV_LFHF': [],
     'A0': [], 'A1': [], 'A2': [], 'A3': [], 'A4': [],
     'C0': [], 'C1': [], 'C2': [], 'C3': [], 'C4': [],
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

def ecg_metrics(file, name, d):
    d['ID'].append(name)
    # w += 1
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
        rpeaks = ecg.christov_segmenter2(signal=ecg_normal, sampling_rate=fs)
        for k, v in time_domain(ecg_normal).items():
            d[k].append(v)
        d = frequency_domain(rpeaks, fs, d)
    if 'stressors' in name:
        d['STRESS'].append('YES')
    else:
        d['STRESS'].append('NO')
    return d


def eda_metrics(file, d):
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
    for i in range(len(coef1)):
        if i == 0:
            pass
        else:
            c1 = coef1[::int(len(coef1) / i)]
            if len(c1) == 5:
                break
    for index, item in enumerate(c1):
        if index < 5:
            d['C' + str(index)].append(float(item))

    for i in range(len(area1)):
        if i == 0:
            pass
        else:
            a1 = area1[::int(len(area1) / i)]
            if len(a1) == 5:
                break
    for index, item in enumerate(a1):
        if index < 5:
            d['A' + str(index)].append(float(item))
    return d


#########################
#        REST           #
#########################
for file in glob.glob("data/bitalino/ecg/normal/*.txt", ):
    name = Path(file).stem
    d = ecg_metrics(file, name, d)
    with open("data/bitalino/eda/normal/" + name + ".txt"):
        d = eda_metrics(file, d)

##########################
#        STRESS          #
##########################
for file in glob.glob("data/bitalino/ecg/stressed/*.txt", ):
    name = Path(file).stem
    d = ecg_metrics(file, name, d)
    with open("data/bitalino/eda/stressed/" + name + ".txt"):
        d = eda_metrics(file, d)

print(d)
df = pd.DataFrame(d)
df.to_csv(path_or_buf='../data/ml_metrics_datasets/datasets_fiveVal/9_40_9.csv', index=False)

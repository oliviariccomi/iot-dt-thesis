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
from scipy.interpolate import interp1d
from scipy.integrate import trapz
from scipy import signal
from scipy.stats import ttest_rel

def td (rr):
    results = {}
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


d = {'ID': [], 'Mean RR (ms)': [], 'STD RR/SDNN (ms)': [], 'Mean HR (Kubios\' style) (beats/min)': [],
     'Mean HR (beats/min)': [], 'STD HR (beats/min)': [], 'Min HR (beats/min)': [],
     'Max HR (beats/min)': [], 'RMSSD (ms)': [], 'NNxx': [], 'pNNxx (%)': [],
     #'HRV_LF': [],
     #'HRV_HF': [],
     #'HRV_LFHF': [],
     'STRESS': []}

for file in glob.glob("../data/polar/polar_rest/*.csv", ):
    name = Path(file).stem
    colnames = ['time',	'HeartRate', 'RR']
    data = pd.read_csv(file, names=colnames, sep="\t")
    d['ID'].append(name)
    if "rest" in name:
        d['STRESS'].append("NO")
    data['RR'].pop(0)
    data['time'].pop(0)
    r = []
    p = []
    for i in data['RR'].values:
        r.append(float(i))
    rr = np.array(r)
    for j in data['time'].values:
        p.append(float(j))


    # Time domain
    for k, v in td(rr).items():
        d[k].append(v)

for file in glob.glob("../data/polar/polar_stress/*.csv", ):
    name = Path(file).stem
    colnames = ['time',	'HeartRate', 'RR']
    data = pd.read_csv(file, names=colnames, sep="\t")
    d['ID'].append(name)
    if "stress" in name:
        d['STRESS'].append("YES")
    data['RR'].pop(0)
    data['time'].pop(0)
    r = []
    p = []
    for i in data['RR'].values:
        r.append(float(i))
    rr = np.array(r)
    for j in data['time'].values:
        p.append(float(j))
    rpeaks = np.array(p)

    for k, v in td(rr).items():
        d[k].append(v)

    # Frequency domain
    '''hrv_freq = nk.hrv_frequency(rpeaks, sampling_rate=100, show=True)
    d['HRV_LF'].append(hrv_freq['HRV_LF'].values[0])
    d['HRV_HF'].append(hrv_freq['HRV_HF'].values[0])
    d['HRV_LFHF'].append(hrv_freq['HRV_LFHF'].values[0])'''
print(d)
df = pd.DataFrame(d)
df.to_csv(path_or_buf='../data/t_test_metrics_csv/polar_ecg_metrics.csv', index=False)


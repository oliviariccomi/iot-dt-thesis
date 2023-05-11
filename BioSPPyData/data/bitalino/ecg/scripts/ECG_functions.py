import numpy as np
from biosppy.signals import ecg
import neurokit2 as nk


# Funzione che genera gli r_peaks
# Funzione che genera il time peaks
# Funzione che genera gli rr

# Metriche tempo
# Metriche frequenza

def r_peaks(ecg_normal, fs):
    rpeaks_normal = ecg.christov_segmenter2(signal=ecg_normal, sampling_rate=fs)
    return rpeaks_normal

def exp_time(ecg_normal, fs):
    time = []
    for x in range(len(ecg_normal)):
        time.append(x * (1 / fs))
    x = np.asanyarray(time)
    return x

def time_peaks(ecg_normal, rpeaks, x):
    ecg_values = []
    time_peaks = []
    for i in rpeaks:
        ecg_values.append(ecg_normal[i])
        time_peaks.append(x[i])
    return ecg_values, time_peaks

def rr_ms(time_peaks):
    rr = np.diff(time_peaks)
    rr = rr * 1000
    return rr

def rr_all (ecg_normal, fs):
    rpeaks = ecg.christov_segmenter2(signal=ecg_normal, sampling_rate=fs)
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

def time_domain(ecg_normal):
    fs = 100
    results = {}
    rp = r_peaks(ecg_normal, fs)
    t = exp_time(ecg_normal, fs)
    ecg_values, tp = time_peaks(ecg_normal, rp, t)
    rr = rr_ms(tp)

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

def frequency_domain(rpeaks, fs, d):
    hrv_freq = nk.hrv_frequency(rpeaks, sampling_rate=fs, show=True)
    d['HRV_LF'].append(hrv_freq['HRV_LF'].values[0])
    d['HRV_HF'].append(hrv_freq['HRV_HF'].values[0])
    d['HRV_LFHF'].append(hrv_freq['HRV_LFHF'].values[0])
    return d
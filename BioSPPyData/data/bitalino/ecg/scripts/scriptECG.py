import biosppy
from biosppy import storage
from biosppy.signals import ecg
import matplotlib as mpl
import matplotlib.pyplot as plt
import pandas as pd
import numpy as np
import neurokit2 as nk
from scipy.signal import resample
import heartpy as hp
import sys
import pickle
import time
import ecg_plot
mpl.use('TkAgg')


sys.path.insert(0, "/Users/olivia1/Library/Mobile Documents/com~apple~CloudDocs/ROBA TESI/CODES/CODICI "
                   "ECG/BioSPPyLibrary/biosppy")
print(sys.path)
# load raw ECG signal

from scipy.ndimage import label
from scipy.stats import zscore
from scipy.interpolate import interp1d
from scipy.integrate import trapz


y_max = 0
y_min = 10000

pd.set_option("display.max_rows", None, "display.max_columns", None)
name = "alice"

ecg_normal, mdata = biosppy.storage.load_txt('../normal/'+name+'.txt')
# out = ecg.ecg(signal=signal, sampling_rate=100, show=True)
rpeaks_normal = ecg.christov_segmenter2(signal=ecg_normal, sampling_rate=100)
#r_signal = ecg.correct_rpeaks2(signal=signal, rpeaks=rp, sampling_rate=100, tol=0.05);
for i in ecg_normal:
    if i > y_max:
        y_max = i
    if i < y_min:
        y_min = i

ecg_stress, m = biosppy.storage.load_txt('../stressed/' + name + '_stressors.txt')
# o = ecg.ecg(signal=s, sampling_rate=100, show=True)
rpeaks_stress = ecg.christov_segmenter2(signal=ecg_stress, sampling_rate=100)

for z in ecg_stress:
    if z > y_max:
        y_max = z
    if z < y_min:
        y_min = z

print(y_max, y_min)

# R peaks normal person
estimated_data_normal = pd.read_csv("../normal/" + name + ".txt", header=None)
coln = estimated_data_normal[:][0]
fig = plt.figure(figsize=(20, 15))
plt.title("ECG signal of normal person with R-peaks 9_40_9", fontsize=24)
plt.plot(coln, lw=0.4, alpha=0.4, color="brown")
plt.plot(coln[rpeaks_normal], marker="o", ls="", ms=3)
plt.ylim(y_min-0.1, y_max+0.1)
plt.xlabel("Time (s)", fontsize=15)
plt.ylabel("Amplitude", fontsize=15)
#pickle.dump(fig, open('../../../../plots/peaks_9_40_9.fig.pickle', 'wb'))
#figx = pickle.load(open('../../../../plots/ecg_5_20_o8.fig.pickle', 'rb'))
#fig.show()

with open('../../../../plots/peaks_5_15_7.pickle', 'wb') as f: # should be 'wb' rather than 'w'
    pickle.dump(fig, f)
fig.show()





'''est_data_new = pd.read_csv("../stressed/" + name + "_stressors.txt", header=None)
cols = est_data_new[:][0]
plt.subplot(212)
plt.title("ECG signal of stressed person with R-peaks", fontsize=24)
plt.plot(cols, lw=0.4, alpha=0.4)
plt.plot(cols[rpeaks_stress], marker="x", ls="", ms=3)
plt.ylim(y_min-0.1, y_max+0.1)
plt.xlabel("Time (s)", fontsize=15)
plt.ylabel("Amplitude", fontsize=15)
plt.show()'''

## Serie RR

# 1) Creo vettore time con i tempi
time = []
for x in range(len(ecg_normal)):
    time.append(x * (1 / 100))
x = np.asanyarray(time)

#print("x:"+str(time))
#print("Lenghth of time array: " +str(len(time)))
#print("Length of ecg array: "+str(len(signal)))


# 2) Trovo i valori degli indiic dei picchi R nell'array ecg e i corrispettivi valori nel tempo
ecg_values = []
time_peaks = []
for i in rpeaks_normal:
    ecg_values.append(ecg_normal[i])
    time_peaks.append(x[i])

print("Ecg values with peaks: " + str(ecg_values))
# print(time_r)
print("Respective istante of time: " + str(time_peaks))

# 3) trovo serie RR
rr = np.diff(time_peaks)  # rr[x] (s)
# print(rr)

# 4) Find bpm array and plot
'''bpm = []
for x in rr:
    value = 60 / x
    bpm.append(value)


# print(bpm)
plt.figure(figsize=(20, 10))
plt.title("bpm", fontsize=24)
plt.ylim(min(bpm)-10, max(bpm)+10)
plt.xticks(np.arange(0, 250, 50), fontsize=18)
plt.yticks(fontsize=18)
plt.plot(bpm, color="blue")
plt.show()'''


# HRV Metrics time domain
def timedomain(rr):
    results = {}
    rr = rr * 1000
    hr = 60000 / (rr)  # rr is in seconds but rr is in ms

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


print("\n")
print("Time domain metrics:")
for k, v in timedomain(rr).items():
    print("- %s: %.2f" % (k, v))

# HRV Metrics frequency domain
print("\n")
print("Frequency domain metrics:")
hrv_freq = nk.hrv_frequency(rpeaks_normal, sampling_rate=100, show=False)
df1 = hrv_freq[['HRV_LF', 'HRV_HF', 'HRV_LFHF']]
print(df1)
plt.plot(hrv_freq)
plt.ylim(-0.5, 1.5)
plt.show()




# Heart rate
hrdata = hp.get_data('../normal/'+name+'.txt')
filtered = hp.remove_baseline_wander(hrdata, 100)
wd, m = hp.process(hp.scale_data(filtered), 100)
# hp.plotter(wd, m, figsize=(20, 10))
print("\n")
for measure in m.keys():
    print('%s: %f' % (measure, m[measure]))

resampled_signal = resample(filtered, len(filtered) * 4)
wd, m = hp.process(hp.scale_data(resampled_signal), 100 * 4)

#hp.plotter(wd, m, figsize=(20, 10))

for measure in m.keys():
    print('%s: %f' % (measure, m[measure]))

def ecg_csv (ecg_array):
    r = ecg.christov_segmenter2(signal=ecg_array, sampling_rate=100)
    t = []
    for x in range(len(ecg_normal)):
        t.append(x * (1 / 100))
    a = np.asanyarray(t)
    values = []
    peaks = []
    for i in r:
        values.append(ecg_array[i])
        peaks.append(a[i])

    # Time metrics
    rr_se = np.diff(peaks)
    dict = {}

    for k, v in timedomain(rr_se).items():
        print("- %s: %.2f" % (k, v))
    hr = nk.hrv_frequency(r, sampling_rate=100, show=True)
    df2 = hr[['HRV_LF', 'HRV_HF', 'HRV_LFHF']]

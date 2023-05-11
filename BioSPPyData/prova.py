import glob

from biosppy import storage
from biosppy.signals import ecg
import neurokit2 as nk
import random
from random import randint
import pandas as pd
pd.set_option('display.max_columns', None)  # or 1000
pd.set_option('display.max_rows', None)  # or 1000
pd.set_option('display.max_colwidth', None)

randomlist = random.sample(range(1, 90), 31)
print(randomlist)
'''fs = 100
step = 5  # in secondi
len_window = 20  # in secondi
d = pd.read_csv("data/bitalino/ecg/normal/alessia.txt")

df = {'ecg_signal': [], 'r_peaks': [], 'time_indexed_peaks': [], 'rr_series': []}

#####################
#    VARIABLES      #
#####################
read_file = pd.read_csv('data/bitalino/ecg/normal/alessia.txt')
read_file.to_csv('./data/ecg/new.csv', index=None)
ecg_normal, mdata = storage.load_txt("data/bitalino/ecg/normal/alessia.txt")

with open("data/bitalino/ecg/normal/prova.txt", "w") as file:
    for i in rr_all(ecg_normal, fs):
        file.write((str(i)) + "\n")

read_file = pd.read_csv('data/bitalino/ecg/normal/prova.txt')
read_file.to_csv('./data/ecg/new.csv', index=None)'''


'''
#############################
#         FUNCTIONS         #
#############################
# ecg_normal: array of ecg values
# rpeaks_normal: array of the peaks location indexes
def timedomain(ecg_normal, rpeaks_normal):
    results = {}
    time = []
    # Array of time: I need an array of time with the same length of ecg normal (expressed in ms)
    for x in range(len(ecg_normal)):
        time.append(x * (1 / fs))
    x = np.asanyarray(time)

    ecg_values = [] # Here I append the ecg values corresponded to each peak
    time_peaks = [] # Here I append the istant of time of each peak
    for i in rpeaks_normal:
        ecg_values.append(ecg_normal[i])
        time_peaks.append(x[i])


    rr = np.diff(time_peaks) # rr series is an array with the time difference between one peak and another
    print("rr series in s",rr)
    rr = rr * 1000 # because I want in milliseconds and (right now) rr series refers to the difference of time between two values expressed in seconds
    print("rr series in ms:", rr)
    hr = 60000 / (rr)  # heart rate is an array of

    # suppongo di avere rr in millisecondi quindi facendo 60000 / rr (ovvero i miei valori di rr

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


# w = 0
#############################
#        REST ECG           #
#############################

ecg_normal, mdata = storage.load_txt("data/bitalino/ecg/normal/eugenio.txt")
rpeaks_normal = ecg.christov_segmenter2(signal=ecg_normal[0:11000], sampling_rate=100)
hrv_freq = nk.hrv_frequency(rpeaks_normal, sampling_rate=100, show=False)
print(hrv_freq)

if ecg_normal.size < 2: # because in the csv there might be only "np.nan"
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
    for k, v in timedomain(ecg_normal, rpeaks_normal).items():
        d[k].append(v)
    hrv_freq = nk.hrv_frequency(rpeaks_normal, sampling_rate=fs, show=True)
    d['HRV_LF'].append(hrv_freq['HRV_LF'].values[0])
    d['HRV_HF'].append(hrv_freq['HRV_HF'].values[0])
    d['HRV_LFHF'].append(hrv_freq['HRV_LFHF'].values[0])
d['STRESS'].append('NO')

    #############################
    #        REST EDA           #
    #############################
Y = pd.read_csv("./data/ecg/normal/alessia.txt")
Z = [] # this is still the array of time with same file length (expressed in ms))
for i in range(len(Y)):
    Z.append(i * (1 / fs))

Y = Y.values # eda values
X = pd.DataFrame(Z).values # series of time
lenX = len(X) # how many istants of time
arrX = X[0: (len_window * fs - 1)]  # time window
coef1 = []
area1 = []

for i in range(0, lenX - (len_window * fs - 1), step * fs):
    ll = LinearRegression()
    arrY = Y[i:i + (len_window * fs) - 1] # finestra temporale dei tempi

    ll.fit(arrX, arrY)
    # plt.plot(arrX, ll.coef_ * arrX + ll.intercept_, linewidth=1)

    arrY_test = ll.coef_ * arrX + ll.intercept_ # pendenza della retta
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



# CHECK VALUES

i = 0
for file in glob.glob("./data/bitalino/ecg/normal/*.txt"):
    i += 1
print("Ecg normal"+str(i))

j = 0
for file in glob.glob("./data/bitalino/ecg/stressed/*.txt"):
    j += 1
print("Ecg stress"+str(j))

k = 0
for file in glob.glob("./data/bitalino/eda/normal/*.txt"):
    k += 1
print("Eda normal"+str(k))

y = 0
for file in glob.glob("./data/bitalino/eda/stressed/*.txt"):
    y += 1
print("Eda Stress"+str(y))


for file in glob.glob("data/bitalino/ecg/normal/eugenio.txt", ):
    ecg_normal, mdata = storage.load_txt(file)
    rpeaks_normal = ecg.christov_segmenter2(ord=7, hf=9, lf=40, signal=ecg_normal, sampling_rate=100)
    with open("data/bitalino/rpeaks_e.txt", "w") as f:
        f.write(str(rpeaks_normal))

d = {'ID': [], 'Mean RR (ms)': [], 'STD RR/SDNN (ms)': [], 'Mean HR (Kubios\' style) (beats/min)': [],
     'Mean HR (beats/min)': [], 'STD HR (beats/min)': [], 'Min HR (beats/min)': [],
     'Max HR (beats/min)': [], 'RMSSD (ms)': [], 'NNxx': [], 'pNNxx (%)': [], 'HRV_LF': [], 'HRV_HF': [],
     'HRV_LFHF': [],
     'A0': [], 'A1': [], 'A2': [], 'A3': [], 'A4': [],
     'C0': [], 'C1': [], 'C2': [], 'C3': [], 'C4': [],
     'STRESS': []}

valori_basali = {}

#####################
#    VARIABLES      #
#####################
fs = 100
step = 5  # in secondi
len_window = 20  # in secondi
order = 7
hf = 9
# alta
lf = 40


#############################
#         FUNCTIONS         #
#############################

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


#############################
#        REST ECG           #
#############################
for file in glob.glob("data/bitalino/ecg/normal/nicola.txt", ):
    name = Path(file).stem
    d['ID'].append(name)
    ecg_normal, mdata = storage.load_txt(file)
    ecg_basale = ecg_normal[:2999]
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
        rpeaks_normal = ecg.christov_segmenter2(ord=order, hf=hf, lf=lf, signal=ecg_normal, sampling_rate=fs)
        rpeaks_basale = ecg.christov_segmenter2(ord=order, hf=hf, lf=lf, signal=ecg_basale, sampling_rate=fs)
        if name not in valori_basali:
            valori_basali[name] = []
            for k, v in timedomain(ecg_basale).items():
                valori_basali[name].append(v)

        keys = list(d.keys())[1:]
        for t, c in timedomain(ecg_normal).items():
            basal_remove = valori_basali[name][keys.index(t)]
            new_val = c - basal_remove
            d[t].append(new_val)

        hrv_freq_basale = nk.hrv_frequency(rpeaks_basale, sampling_rate=fs, show=False)
        valori_basali[name].append(hrv_freq_basale['HRV_LF'].values[0])
        valori_basali[name].append(hrv_freq_basale['HRV_HF'].values[0])
        valori_basali[name].append(hrv_freq_basale['HRV_LFHF'].values[0])

        hrv_freq = nk.hrv_frequency(rpeaks_normal, sampling_rate=fs, show=False)
        d['HRV_LF'].append(hrv_freq['HRV_LF'].values[0] - valori_basali[name][10])
        d['HRV_HF'].append(hrv_freq['HRV_HF'].values[0] - valori_basali[name][11])
        d['HRV_LFHF'].append(hrv_freq['HRV_LFHF'].values[0] - valori_basali[name][12])
    d['STRESS'].append('NO')

    #############################
    #        REST EDA           #
    #############################
    with open("data/bitalino/eda/normal/"+name+".txt") as f:
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
        print(coef1)
        valori_basali[name].append(coef1[0])
        five_split_coef = np.array_split(coef1[1:], 5)
        for index, item in enumerate(five_split_coef):
            med = mean(item)
            basal = valori_basali[name][13]
            value = med - basal
            d['C' + str(index)].append(mean(item) - valori_basali[name][13])


        valori_basali[name].append(area1[0])
        five_split_area = np.array_split(area1[1:], 5)
        for index, item in enumerate(five_split_area):
            d['A' + str(index)].append(mean(item) - valori_basali[name][14])
'''
# w = 0
#############################
#        STRESS ECG         #
#############################
'''
for file in glob.glob("data/bitalino/ecg/stressed/nicola_stressors.txt", ):
    name = Path(file).stem
    nn = name.split("_")[0]
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
        rpeaks_normal = ecg.christov_segmenter2(ord=order, hf=hf, lf=lf, signal=ecg_normal, sampling_rate=fs)
        key = list(d.keys())[1:]
        for r, w in timedomain(ecg_normal).items():
            b_remove = valori_basali[nn][key.index(r)]
            new_v = w - b_remove
            d[r].append(new_v)

        hrv_freq = nk.hrv_frequency(rpeaks_normal, sampling_rate=fs, show=False)
        d['HRV_LF'].append(hrv_freq['HRV_LF'].values[0] - valori_basali[nn][10])
        d['HRV_HF'].append(hrv_freq['HRV_HF'].values[0] - valori_basali[nn][11])
        d['HRV_LFHF'].append(hrv_freq['HRV_LFHF'].values[0] - valori_basali[nn][12])
    d['STRESS'].append('YES')

    #############################
    #        STRESS EDA         #
    #############################
    with open("data/bitalino/eda/stressed/"+name+".txt") as f:
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
            d['C' + str(index)].append(mean(item) - valori_basali[nn][13])

        five_split_area_str = np.array_split(area2, 5)
        for index, item in enumerate(five_split_area_str):
            d['A' + str(index)].append(mean(item) - valori_basali[nn][14])


print(d)
#df = pd.DataFrame(d)
#df.to_csv(path_or_buf='../data/ml_metrics_datasets/datasets_fiveVal_valoriBasali/'+str(hf)+'_'+str(lf)+'_'+str(order)+'_vb.csv', index=False)
'''
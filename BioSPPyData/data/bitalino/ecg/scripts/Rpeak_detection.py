from biosppy import storage
from biosppy.signals import ecg
import matplotlib.pyplot as plt

# load raw ECG signal
signal, mdata = storage.load_txt('stressed/ecg_luca_stressors.txt')

# R-peak Gamboa_Segmenter algorithm
r_peak1 = ecg.gamboa_segmenter(signal=signal, sampling_rate=100, tol=0.002)

# R-peak Christov-Segmenter algorithm
r_peak2 = ecg.christov_segmenter(signal=signal, sampling_rate=100)

# R-peak Hamilton-Segmenter algorithm
r_peak3 = ecg.hamilton_segmenter(signal=signal)

# R-peak ASI-Segmenter algorithm
r_peak4 =ecg.ASI_segmenter(signal=signal, sampling_rate=100)

print(r_peak4)

# Corrected R-peak
#corrected_peaks = ecg.correct_rpeaks(signal=signal, rpeaks=r_peak4, sampling_rate=100, tol=0.05)

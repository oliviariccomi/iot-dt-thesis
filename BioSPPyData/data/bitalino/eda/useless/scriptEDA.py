import pandas as pd
import matplotlib.pyplot as plt
import matplotlib as mpl
import seaborn as sns
import numpy as np
from numpy import *
from sklearn import linear_model
from sklearn.metrics import mean_absolute_error
from sklearn.linear_model import LinearRegression
from biosppy import plotting, eda
import math



Y = pd.read_csv('../normal/alice.txt')
Z = []
for x in range(len(Y)):
    Z.append(x * (1 / 100))
U = []
for k in Y.values:
    U.append(float(k))
Y = Y.values
X = pd.DataFrame(Z).values

fs = 100
step = 5  # in secondi
len_window = 20  # in secondi
lenX = len(X)
## Controllare se parte da 0 o 1
# Assumo che parta da 0, nel caso cambio a 1
# Se parte da 1 tolgo -1
# arrX = X[1: len_window * fs]
arrX = X[0: (len_window * fs - 1)]  # arrX in realtà diventa un array generato tipo time_r dell'ecg che ha 0, 0.01, 0.02,...
# che va da 0 a lenY /

# Window indexes
# for va da 1 a 20 con step di 5
for i in range(0, lenX - (len_window * fs - 1), step * fs):
    # print(i)
    ll = LinearRegression()
    arrY = Y[i:i + (len_window * fs) - 1]

    # fitto con gli indici della finestra e corrispondenti valori
    ll.fit(arrX, arrY)
    plt.plot(arrX, ll.coef_ * arrX + ll.intercept_, linewidth=1)
    arrY_test = ll.coef_ * arrX + ll.intercept_

    # SECONDO ME ARRY TEST E ARRY DEVONO ESSERE NORMALIZZATI


    ## 1° feature: slope
    # print(ll.coef_)
    # print(sum(abs(arrY-arrY_test)))
    # plotto di arrX, arrY ---> curva verde del file
    # sullo stesso plot arrX e arrY yest
    # In questo modo verifico che io abbia calcolato la retta giusta

    ## 2° feature: area
    area = sum(abs(arrY_test - arrY))

    '''plt.plot(arrX, arrY)
    plt.show()'''

#ts, filtered, onsets, peaks, amplitudes = eda.eda(np.array(U),100,show=False)
#plotting.plot_eda(ts,np.array(U), filtered, onsets, peaks, amplitudes, show=True)
plt.figure(figsize=(10, 6))
plt.title("EDA signal of normal person", fontsize=16)
plt.plot(np.array(U), label='raw')
plt.ylabel('Amplitude')
plt.xlabel('Time(s)')
plt.legend()
plt.show()
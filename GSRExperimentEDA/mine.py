import pandas as pd
import matplotlib.pyplot as plt
import matplotlib as mpl
import seaborn as sns
import numpy as np
from numpy import *
from sklearn import linear_model
from sklearn.metrics import mean_absolute_error
from sklearn.linear_model import LinearRegression
import math
a = 151731
Y = pd.read_('eda/normal/correct/t.xlsx')
X = pd.read_excel('eda/timeFiles/1.xlsx')
X = X.values
Y = Y.values
lenX = len(X)
iterations = math.floor(lenX / 5)
arrX = X[1: 20]

# for va da 1 a 20 con step di 5
for i in range(0, lenX - 19, 5):
    # print(i)
    ll = LinearRegression()

    arrY = Y[i:i + 19]

    ll.fit(arrX, arrY)
    plt.plot(arrX, ll.coef_ * arrX + ll.intercept_, linewidth=1)
    arrY_test = ll.coef_ * arrX + ll.intercept_

    ## 1° feature: intercept (ll.intercept)
    print(ll.intercept_)
    # print(sum(abs(arrY-arrY_test)))

    ## 2° feature: area
    area = arrY_test - arrY
    print(area)

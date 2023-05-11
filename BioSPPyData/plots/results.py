import pandas as pd
import numpy as np
import seaborn as sns
import matplotlib.pyplot as plt

d = pd.read_csv("../data/ml_metrics_datasets/accuracy/accuracy.csv")
'''plt.figure(figsize=(20, 5))
plt.plot(x="CSV", y=["Decision Tree", "Logistic Regression", "KNN", "SVC", "Naive Bayes"], kind="bar")
plt.subplots_adjust(top=.9, left=.10, bottom=.2, right=.93)
plt.legend(loc='upper left', bbox_to_anchor=(1, 0.5), fancybox=True)
plt.show()'''

g = d.plot(x='CSV', y=["Decision Tree", "Logistic Regression", "KNN", "SVC", "Naive Bayes"], kind='bar', figsize=(15,8), grid=True)
plt.subplots_adjust(left=.04, right=.88, top=.97, bottom=.15)
plt.legend(title="Algorithms", loc="center right", bbox_to_anchor=(1.15, 0.5))
plt.xticks(fontsize=12, rotation=70)
plt.yticks(fontsize=12)
plt.savefig("../data/ml_metrics_datasets/accuracy.png")
plt.show()
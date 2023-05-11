# t-test for independent samples
from math import sqrt
from numpy.random import seed
from numpy.random import randn
from numpy import mean
from scipy.stats import sem
from scipy.stats import t
import pandas as pd
from scipy.stats import ttest_ind
from scipy.stats import ttest_ind
from scipy.stats import ttest_rel
import matplotlib.pyplot as plt
import seaborn as sns
import numpy as np
import pandas as pd
import numpy as np

pd.set_option("display.max_rows", None, "display.max_columns", None)

# generate two independent samples
d = pd.read_csv("../data/ml_metrics_datasets/csvFile_mean_9_40_7.csv")

t_test = {'Mean RR (ms)': [], 'STD RR/SDNN (ms)': [], 'Mean HR (Kubios\' style) (beats/min)': [],
          'Mean HR (beats/min)': [], 'STD HR (beats/min)': [], 'Min HR (beats/min)': [],
          'Max HR (beats/min)': [], 'RMSSD (ms)': [], 'NNxx': [], 'pNNxx (%)': [], 'HRV_LF': [], 'HRV_HF': [],
          'HRV_LFHF': [],
          'LL.COEF': [],
          'AREA': []
          }

d.iloc[:, 1: 16] = d.iloc[:, 1: 16].apply(lambda x: x.fillna(x.mean()), axis=0)
# print(d)
p_v = []
p_tot = 0
for i in t_test.keys():
    a = d.query('STRESS == "NO"')[i]
    b = d.query('STRESS == "YES"')[i]
    t_val, p_val = ttest_rel(a, b)
    t_test[i].append(t_val)
    t_test[i].append(p_val)
    p_tot += p_val
    p_v.append(p_val)
    if p_val < 0.1:
        t_test[i].append('Can Reject NULL HYP: OK')
    else:
        t_test[i].append('Cannot Reject NULL HYP')
print("T test results for features: ", t_test)
print("\nAverage p-value:", str(p_tot/15))

fig, ax = plt.subplots()
sns.kdeplot(p_v, shade=True)
'''sns.barplot(x=var["incentive"], y=var["actual_productivity"], palette="rocket")'''
ax.axvline(mean(p_v), linewidth=1, color="red", label="Mean P value")
plt.legend()
plt.xlabel("P Value")
plt.show()
'''df = pd.DataFrame(t_test)
df.to_csv(path_or_buf='../data/t_testVal.csv', index=False)'''

'''
The paired sample t-test is also called dependent sample t-test. 
It’s an univariate test that tests for a significant 
difference between 2 related variables. 
(I want to detect stress for an individual before 
and after some treatment, condition, or time point)
'''


'''The paired test measures whether the average score differs
significantly across samples(e.g.exams).If we
observe a large
p - value, greater than 0.05 or 0.1
then we cannot reject the null hypothesis
of identical average scores.
If the p-value is smaller than the threshold,
e.g.1 %, 5 % or 10 %, then we reject
the null hypothesis of equal averages --> reject null hypotesis = the true mean test score is different 
for rest and stress = two different population'''


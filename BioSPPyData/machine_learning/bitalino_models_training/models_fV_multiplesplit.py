from statistics import mean

import pandas as pd
import numpy as np
import glob
import os

from sklearn.linear_model import LogisticRegression
from sklearn.svm import SVC
from sklearn.metrics import accuracy_score
from sklearn.naive_bayes import BernoulliNB, GaussianNB
from sklearn.neighbors import KNeighborsClassifier

from machine_learning import cross_validation, evaluation
from sklearn.model_selection import train_test_split, ShuffleSplit
from sklearn.tree import DecisionTreeClassifier
from sklearn.preprocessing import StandardScaler, LabelBinarizer

pd.set_option('display.max_columns', None)  # or 1000
pd.set_option('display.max_rows', None)  # or 1000
pd.set_option('display.max_colwidth', None)

SEED = 42

if __name__ == '__main__':
    accuracy = {'CSV': [], 'HP': [], 'LP': [], 'ORDER': [], 'Decision Tree': [], 'Logistic Regression': [],
                'KNN': [], 'SVC': [], 'Naive Bayes': []}
    cv = ShuffleSplit(n_splits=5, random_state=np.random, test_size=0.2)
    for file in glob.glob("/Users/olivia1/Desktop/thesis-management-master/BioSPPyData/data/ml_metrics_datasets/*.csv", ):
        accuracies = {'Decision Tree': [], 'Logistic Regression': [],
                'KNN': [], 'SVC': [], 'Naive Bayes': []}
        d = pd.read_csv(file)
        head, tail = os.path.split(file)
        k = str(tail)
        accuracy['CSV'].append(k)
        accuracy['HP'].append(k.split("_")[0])
        accuracy['LP'].append(k.split("_")[1])
        accuracy['ORDER'].append(k.split("_")[2].split(".csv")[0])
        # d.drop(['ID'], axis=1, inplace=True)
        d.iloc[:, 1: 16] = d.iloc[:, 1: 16].apply(lambda x: x.fillna(x.mean()), axis=0)
        d.drop(['ID'], axis=1, inplace=True)
        output_dictionary = {'YES': 0, 'NO': 1}

        ####################################
        #            MODELS                #
        ####################################
        models = ["Decision Tree", "Logistic Regression", "KNN", "SVC",
                  "Naive Bayes"]

        ####################################
        #  FEATURE VECTOR E TARGET VECTOR  #
        ####################################
        X = d.drop(columns='STRESS')
        y = d['STRESS'].replace(output_dictionary)

        ##########################
        #    SPLITTING DATASET   #
        ##########################
        #X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=SEED)
        print("Training phase", k)
        for train_index, test_index in cv.split(X):
            X_train, X_test = X.values[train_index], X.values[test_index]
            y_train, y_test = y.values[train_index], y.values[test_index]

            scaler = StandardScaler()
            X_train_scaled = scaler.fit_transform(X_train)
            X_test_scaled = scaler.transform(X_test)

            ###############
            #    MODELS   #
            ###############
            dt = DecisionTreeClassifier()
            log_reg_model = LogisticRegression()
            knn_model = KNeighborsClassifier()
            svc_model = SVC()
            nb_model = GaussianNB()

            ###########################
            #    CROSS VALIDATION     #
            ###########################
            parameters_dt = {'criterion': ['gini', 'entropy'], 'max_depth': np.arange(3, 15)}
    
            parameters_lg = {'C': [0.001, 0.01, 0.1, 1, 5, 10, 100, 1000], 'max_iter': [4000]}
    
            parameters_knn = {"n_neighbors": list(range(1, 30, 2)), "weights": ["uniform", "distance"]}
    
            parameters_svc = {"C": list(range(1, 20)), "kernel": ["linear", "poly", "rbf"]}
    
            BEST_PARAM_DT, best_estimator_dt = cross_validation.dtree_grid_search(X_train_scaled, y_train,
                                                                                  parameters_dt)
            #print("Best DT params:", BEST_PARAM_DT)
            BEST_PARAM_LOGREG, best_estimator_logreg = cross_validation.cv(X_train_scaled, y_train,
                                                           log_reg_model, parameters_lg, "LOG_REG")
            #print("Best LG params:", BEST_PARAM_LOGREG)
            BEST_PARAM_KNN, best_estimator_knn = cross_validation.cv(X_train_scaled, y_train,
                                                            knn_model, parameters_knn, "KNN")
            #print("Best KNN params:", BEST_PARAM_KNN)
            BEST_PARAM_SVC, best_estimator_svc = cross_validation.cv(X_train_scaled, y_train,
                                                             svc_model, parameters_svc, "SVC")
            #print("Best SVC params:", BEST_PARAM_SVC)
            #############################
            #    MODEL WITH BEST HP     #
            #############################
            dt_model_final = best_estimator_dt
            logreg_model_final = best_estimator_logreg
            knn_model_final = best_estimator_knn
            svc_model_final = best_estimator_svc
            nb_model.fit(X_train_scaled, y_train)

            #############################
            #       PREDICTION          #
            #############################
            y_predicted = []

            ypred_dt = dt_model_final.predict(X_test_scaled)
            y_predicted.append(ypred_dt)
    
            ypred_logreg = logreg_model_final.predict(X_test_scaled)
            y_predicted.append(ypred_logreg)
    
            ypred_knn = knn_model_final.predict(X_test_scaled)
            y_predicted.append(ypred_knn)
    
            ypred_svc = svc_model_final.predict(X_test_scaled)
            y_predicted.append(ypred_svc)

            ypred_nb = nb_model.predict(X_test_scaled)
            y_predicted.append(ypred_nb)

        #############################
        #       EVALUATION          #
        #############################
            #evaluation.evaluate(y_test, y_predicted)
            #  Binarize in order to plot ROC curve and Precision vs Recall curve
            le = LabelBinarizer()
            y_test_bin = le.fit_transform(y_test)
            for i in range(len(y_predicted)):
                y_pred = le.fit_transform(y_predicted[i])
                #evaluation.pr_roc(y_test_bin, y_pred, models[i])
                #accuracy[models[i]].append(accuracy_score(y_test_bin, y_pred))
                accuracies[models[i]].append(float(accuracy_score(y_test_bin, y_pred)))
        #print(accuracies)
        for t in models:
            accuracy[t].append(mean(accuracies[t]))

    print(accuracy)
'''ac = pd.DataFrame(accuracy)
ac.to_csv(path_or_buf='../../data/ml_metrics_datasets/accuracy/accuracy_val_mean.csv', index=False)'''


'''
fn=['Mean RR (ms)', 'STD RR/SDNN (ms)', 'Mean HR (Kubios\' style) (beats/min)',
          'Mean HR (beats/min)', 'STD HR (beats/min)', 'Min HR (beats/min)',
          'Max HR (beats/min)', 'RMSSD (ms)', 'NNxx', 'pNNxx (%)', 'HRV_LF', 'HRV_HF',
          'HRV_LFHF', 'LL.COEF', 'AREA']
cn=['NO', 'YES']
fig, axes = plt.subplots(nrows = 1,ncols = 1,figsize = (4,4), dpi=300)
tree.plot_tree(model,
               feature_names = fn,
               class_names=cn,
               filled = True);
#fig.savefig('imagename.png')
plt.show()'''

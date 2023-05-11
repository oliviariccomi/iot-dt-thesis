import pickle
import random
from random import randint
from statistics import mean

import joblib
import pandas as pd
import numpy as np
import glob
import os
import sklearn

from sklearn.linear_model import LogisticRegression
from sklearn.svm import SVC
from sklearn.metrics import accuracy_score, recall_score, precision_score
from sklearn.naive_bayes import BernoulliNB, GaussianNB
from sklearn.neighbors import KNeighborsClassifier

from sklearn.model_selection import train_test_split, ShuffleSplit
from sklearn.tree import DecisionTreeClassifier
from sklearn.preprocessing import StandardScaler, LabelBinarizer

from BioSPPyData.machine_learning import cross_validation

pd.set_option('display.max_columns', None)  # or 1000
pd.set_option('display.max_rows', None)  # or 1000
pd.set_option('display.max_colwidth', None)

SEED = 42
randomlist = [35, 54, 31, 61, 80, 86, 7, 19, 11, 12]
# , 6, 66, 15, 85, 73, 88, 32, 79, 36, 65, 29, 50, 78, 37, 49, 63, 3, 55, 67, 69, 40]


def indexes(X, a):
    cv = ShuffleSplit(n_splits=1, random_state=a, test_size=0.2)
    for train_index, test_index in cv.split(X):
        tr = train_index
        te = test_index
    return tr, te


if __name__ == '__main__':
    randomlist = random.sample(range(1, 90), 10)
    print(randomlist)
    recall = {'CSV': [], 'HP': [], 'LP': [], 'ORDER': [], 'Decision Tree': [], 'Logistic Regression': [],
                'KNN': [], 'SVC': [], 'Naive Bayes': []}
    models = ["Decision Tree", "Logistic Regression", "KNN", "SVC", "Naive Bayes"]
    recalls = {}
    for j in range(len(randomlist)):
        a = randomlist[j]
        training = 0
        for file in glob.glob("/Users/olivia1/Desktop/thesis-management-master/BioSPPyData/data/ml_metrics_datasets"
                              "/datasets_fiveVal_valoriBasali/*.csv", ):
            # print("training: ", training)
            d = pd.read_csv(file)
            head, tail = os.path.split(file)
            k = str(tail).split("_vb")[0]
            # print("FILE: ", k)
            if k not in recalls and k not in recall['CSV']:
                recalls[k] = {}
                recall['CSV'].append(k)
                recall['HP'].append(k.split("_")[0])
                recall['LP'].append(k.split("_")[1])
                recall['ORDER'].append(k.split("_")[2].split(".csv")[0])
            # d.drop(['ID'], axis=1, inplace=True)
            d.drop(['ID'], axis=1, inplace=True)
            d.iloc[:, :23] = d.iloc[:, :23].apply(lambda x: x.fillna(x.mean()), axis=0)
            output_dictionary = {'YES': 0, 'NO': 1}

            ####################################
            #  FEATURE VECTOR E TARGET VECTOR  #
            ####################################
            X = d.drop(columns='STRESS')
            y = d['STRESS'].replace(output_dictionary)

            ##########################
            #    SPLITTING DATASET   #
            ##########################
            # X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=SEED)

            train_index, test_index = indexes(X, a)
            # print("TEST IND: ", test_index)

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
            if training == 0:
                ###########################
                #    CROSS VALIDATION     #
                ###########################
                parameters_dt = {'criterion': ['gini', 'entropy'], 'max_depth': np.arange(3, 15)}

                parameters_lg = {'C': [0.001, 0.01, 0.1, 1, 5, 10, 100, 1000], 'max_iter': [4000]}

                parameters_knn = {"n_neighbors": list(range(1, 30, 2)), "weights": ["uniform", "distance"]}

                parameters_svc = {"C": list(range(1, 20)), "kernel": ["linear", "poly", "rbf"]}

                BEST_PARAM_DT, best_estimator_dt = cross_validation.cv(X_train_scaled, y_train, dt,
                                                                       parameters_dt, "DT")
                print("Best DT params:", BEST_PARAM_DT)
                BEST_PARAM_LOGREG, best_estimator_logreg = cross_validation.cv(X_train_scaled, y_train,
                                                                               log_reg_model, parameters_lg, "LOG_REG")
                # print("Best LG params:", BEST_PARAM_LOGREG)
                BEST_PARAM_KNN, best_estimator_knn = cross_validation.cv(X_train_scaled, y_train,
                                                                         knn_model, parameters_knn, "KNN")
                # print("Best KNN params:", BEST_PARAM_KNN)
                BEST_PARAM_SVC, best_estimator_svc = cross_validation.cv(X_train_scaled, y_train,
                                                                         svc_model, parameters_svc, "SVC")
                # print("Best SVC params:", BEST_PARAM_SVC)
                #############################
                #    MODEL WITH BEST HP     #
                #############################
                dt_model_final = best_estimator_dt
                print(dt_model_final)
                logreg_model_final = best_estimator_logreg
                knn_model_final = best_estimator_knn
                svc_model_final = best_estimator_svc
                nb_model.fit(X_train_scaled, y_train)

                joblib.dump(dt_model_final, 'dtPickle.pkl')
                joblib.dump(logreg_model_final, 'logPickle.pkl')
                joblib.dump(knn_model_final, 'knnPickle.pkl')
                joblib.dump(svc_model_final, 'svcPickle.pkl')
                joblib.dump(nb_model, 'nbPickle.pkl')

            loaded_model_dt = joblib.load('dtPickle.pkl', mmap_mode='r')
            print(loaded_model_dt)
            loaded_model_log = joblib.load('logPickle.pkl', mmap_mode='r')
            loaded_model_knn = joblib.load('knnPickle.pkl', mmap_mode='r')
            loaded_model_svc = joblib.load('svcPickle.pkl', mmap_mode='r')
            loaded_model_nb = joblib.load('nbPickle.pkl', mmap_mode='r')

            #############################
            #       PREDICTION          #
            #############################
            y_predicted = []

            ypred_dt = loaded_model_dt.predict(X_test_scaled)
            y_predicted.append(ypred_dt)

            ypred_logreg = loaded_model_log.predict(X_test_scaled)
            y_predicted.append(ypred_logreg)

            ypred_knn = loaded_model_knn.predict(X_test_scaled)
            y_predicted.append(ypred_knn)

            ypred_svc = loaded_model_svc.predict(X_test_scaled)
            y_predicted.append(ypred_svc)

            ypred_nb = loaded_model_nb.predict(X_test_scaled)
            y_predicted.append(ypred_nb)

            #############################
            #       EVALUATION          #
            #############################
            # evaluation.evaluate(y_test, y_predicted)
            #  Binarize in order to plot ROC curve and Precision vs Recall curve
            le = LabelBinarizer()
            y_test_bin = le.fit_transform(y_test)
            for i in range(len(y_predicted)):
                y_pred = le.fit_transform(y_predicted[i])
                # accuracy[models[i]].append(accuracy_score(y_test_bin, y_pred))
                if models[i] in recalls[k]:
                    recalls[k][models[i]].append(recall_score(y_test_bin, y_pred))
                else:
                    recalls[k][models[i]] = [recall_score(y_test_bin, y_pred)]
            training += 1
            #print(test_index)
        # print(accuracies)
    for key, val in recalls.items():
        for ii, jj in val.items():
            recall[ii].append(mean(jj))
    print(recall)
    #ac = pd.DataFrame(recall)
    #ac.to_csv(path_or_buf='../../data/ml_metrics_datasets/accuracy/splits/recall_10_singletr.csv', index=False)

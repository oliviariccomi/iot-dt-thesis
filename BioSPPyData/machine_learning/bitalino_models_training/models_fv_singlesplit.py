import pandas as pd
import numpy as np
import glob
import os
import random

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


def indexes(X, a):
    cv = ShuffleSplit(n_splits=1, random_state=a, test_size=0.2)
    for train_index, test_index in cv.split(X):
        tr = train_index
        te = test_index
    return tr, te


if __name__ == '__main__':

    # cv = ShuffleSplit(n_splits=1, random_state=0, test_size=0.2)
    models = ["Decision Tree", "Logistic Regression", "KNN", "SVC", "Naive Bayes"]
    for j in range(10):
        accuracy = {'CSV': [], 'HP': [], 'LP': [], 'ORDER': [], 'Decision Tree': [], 'Logistic Regression': [],
                    'KNN': [], 'SVC': [], 'Naive Bayes': [], 'test_indexs': []}
        a = random.randint(1,99)
        for file in glob.glob(
                "/Users/olivia1/Desktop/thesis-management-master/BioSPPyData/data/ml_metrics_datasets/datasets_fiveVal_valoriBasali/*.csv", ):
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
            size = d.size
            output_dictionary = {'YES': 0, 'NO': 1}

            ####################################
            #  FEATURE VECTOR E TARGET VECTOR  #
            ####################################

            ## FETURE VECTORS
            # Only ECG
            # X = d.drop(columns=['STRESS', 'A0', 'A1', 'A2', 'A3', 'A4', 'C0', 'C1', 'C2', 'C3', 'C4'])

            # Only EDA
            # X = d.drop(columns=['STRESS', 'Mean RR (ms)', 'STD RR/SDNN (ms)', 'Mean HR (Kubios\' style) (beats/min)', 'Mean HR (beats/min)', 'STD HR (beats/min)','Min HR (beats/min)','Max HR (beats/min)','RMSSD (ms)','NNxx','pNNxx (%)','HRV_LF','HRV_HF','HRV_LFHF'])

            # Both
            X = d.drop(columns='STRESS')

            ## TARGET VECTOR
            y = d['STRESS'].replace(output_dictionary)

            ##########################
            #    SPLITTING DATASET   #
            ##########################
            # Different indexes for every file
            #X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=np.random)
            #accuracy['test_indexs'].append(X_test.index)

            '''print("X train indexes", X_train.index)
            print("X test indexes", X_test.index)
            print("y train indexes", y_train.index)
            print("y test indexes", y_test.index)'''

            # Same indexes for every File
            train_index, test_index = indexes(X, a)
            X_train, X_test = X.values[train_index], X.values[test_index]
            y_train, y_test = y.values[train_index], y.values[test_index]
            print("Train index: ", train_index)
            print("Test index", test_index)

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

            BEST_PARAM_DT, best_estimator_dt = cross_validation.cv(X_train_scaled, y_train, dt,
                                                                   parameters_dt, "DT")
            # print("Best DT params:", BEST_PARAM_DT)
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
            # evaluation.evaluate(y_test, y_predicted)
            #  Binarize in order to plot ROC curve and Precision vs Recall curve
            le = LabelBinarizer()
            y_test_bin = le.fit_transform(y_test)
            for i in range(len(y_predicted)):
                y_pred = le.fit_transform(y_predicted[i])
                # evaluation.pr_roc(y_test_bin, y_pred, models[i])
                accuracy[models[i]].append(accuracy_score(y_test_bin, y_pred))

            # CSv di tutti i risultati algoritmi per vedere se migliore

        ac = pd.DataFrame(accuracy)
        ac.to_csv(path_or_buf='../../data/ml_metrics_datasets/accuracy/accuracy_singlesplit_randomseed'+str(j)+'.csv',index=False)

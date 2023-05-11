from sklearn.metrics import make_scorer, precision_score
from sklearn.model_selection import GridSearchCV, StratifiedKFold
from sklearn.tree import DecisionTreeClassifier
from sklearn.linear_model import LogisticRegression
import numpy as np

def cv(X_train, y_train, clf, parameters, model_name):
    model = clf
    grid_search = GridSearchCV(estimator=model, param_grid=parameters, scoring="f1_weighted", cv=5)
    grid_search.fit(X_train, y_train)
    #print(model_name)
    best_params = grid_search.best_params_
    return best_params, grid_search.best_estimator_


def dtree_grid_search(X, y, param_grid, model):
    # decision tree model
    #dtree_model = DecisionTreeClassifier()
    # use gridsearch to test all values
    dtree_gscv = GridSearchCV(model, param_grid, cv=5)
    # fit model to data
    dtree_gscv.fit(X, y)
    return dtree_gscv.best_params_, dtree_gscv.best_estimator_

from sklearn.metrics import classification_report, precision_score, recall_score, f1_score, roc_curve, \
    precision_recall_curve, confusion_matrix

import matplotlib.pyplot as plt

def evaluate(y_test, y_predicted):
    models = [
        "Decision Tree",
        "Logistic Regression", "KNN", "SVC","Naive Bayes"
        ]
    for i in range(len(y_predicted)):
        print("\n", "\n", models[i])
        print("Classification report ", classification_report(y_test, y_predicted[i]))
        print('Precision is ', precision_score(y_test, y_predicted[i], average="weighted"))
        print('Recall is ', recall_score(y_test, y_predicted[i], average="weighted"))
        print('F1-Score is ', f1_score(y_test, y_predicted[i], average="weighted"))
        print("Confusion matrix", confusion_matrix(y_test, y_predicted[i]))


def pr_roc(y_test, y_predicted, name):

    #  Precision Recall curve
    try:
        precision = dict()
        recall = dict()
        for i in range(7):
            precision[i], recall[i], _ = precision_recall_curve(y_test[:, i], y_predicted[:, i])
            plt.plot(recall[i], precision[i], lw=2, label='class {}'.format(i))
        plt.xlabel("Recall")
        plt.ylabel("Precision")
        plt.legend(loc="best")
        plt.title("Precision vs. Recall curve for " + name)
        plt.show()
    except IndexError:
        pass

    #  ROC curve
    fpr = dict()
    tpr = dict()
    try:
        for i in range(7):
            fpr[i], tpr[i], _ = roc_curve(y_test[:, i], y_predicted[:, i])
            plt.plot(fpr[i], tpr[i], lw=2, label='class {}'.format(i))
        plt.xlabel("False positive rate")
        plt.ylabel("True positive rate")
        plt.legend(loc="best")
        plt.title("ROC curve for " + name)
        plt.show()
    except IndexError:
        pass
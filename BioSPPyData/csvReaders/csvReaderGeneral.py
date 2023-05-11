import csv

name = "Dario"
name2 = "dario"

with open("/Users/olivia1/Library/Mobile Documents/com~apple~CloudDocs/ROBA TESI/DATI/PAZIENTI/ECG e EDA/"+name+"_100Hz.csvMakers", "r") as csv_file:
    arrx = []
    csv_reader = csv.reader(csv_file, delimiter='\t')

    # CHANGE NAME BASED ON PERSON
    with open("../data/ecg/normal/"+name2+".txt", "w") as file:
        for lines in csv_reader:
            value = ((((float(lines[6]) / pow(2, 10)) - 0.5) * 3.3) / 1100) * 1000
            file.write((str(round(value, 8)))+"\n")


with open("/Users/olivia1/Library/Mobile Documents/com~apple~CloudDocs/ROBA TESI/DATI/PAZIENTI/ECG e EDA STRESSORS/"+name+"_100Hz_stressors.csvMakers", "r") as csv_file:
    arrx = []
    csv_reader = csv.reader(csv_file, delimiter='\t')

    # CHANGE NAME BASE ON PERSON
    with open("../data/ecg/stressed/"+name2+"_stressors.txt", "w") as file:
        for lines in csv_reader:
            value = ((((float(lines[6]) / pow(2, 10)) - 0.5) * 3.3) / 1100) * 1000
            file.write((str(round(value, 8)))+"\n")


with open("/Users/olivia1/Library/Mobile Documents/com~apple~CloudDocs/ROBA TESI/DATI/PAZIENTI/ECG e EDA/"+name+"_100Hz.csvMakers", "r") as csv_file:
    arrx = []
    csv_reader = csv.reader(csv_file, delimiter='\t')
    # CHANGE NAME BASE ON PERSON
    with open("../data/eda/normal/"+name2+".txt", "w") as file:
        for lines in csv_reader:
            value = (((float(lines[7]) / pow(2, 10)) * 3.3) / 0.132) * pow(10, -6)
            file.write((str(round(value, 10)))+"\n")


with open("/Users/olivia1/Library/Mobile Documents/com~apple~CloudDocs/ROBA TESI/DATI/PAZIENTI/ECG e EDA STRESSORS/"+name+"_100Hz_stressors.csvMakers", "r") as csv_file:
    arrx = []
    csv_reader = csv.reader(csv_file, delimiter='\t')

    # CHANGE NAME BASE ON PERSON
    with open("../data/eda/stressed/"+name2+"_stressors.txt", "w") as file:
        for lines in csv_reader:
            value = (((float(lines[7]) / pow(2, 10)) * 3.3) / 0.132) * pow(10, -6)
            file.write((str(round(value, 10)))+"\n")


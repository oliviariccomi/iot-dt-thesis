import glob
import csv
from pathlib import Path
import numpy as np

def detectDelimiter(csvFile):
    with open(csvFile, 'r') as myCsvfile:
        header = myCsvfile.readline()
        if header.find(";")!=-1:
            return ";"
        if header.find("\t")!=-1:
            return "\t"

for file in glob.glob("/Users/olivia1/Library/Mobile Documents/com~apple~CloudDocs/ROBA TESI/DATI/PAZIENTI/ECG e EDA/*"):
    with open(file, "r") as csv_file:
        name = Path(file).stem
        csv_reader = csv.reader(csv_file, delimiter=detectDelimiter(file))

        with open("./data/rest/"+name+".txt", "w") as file:
            for lines in csv_reader:
                if lines[6] == '':
                    ecg = np.nan
                else:
                    ecg = ((((float(lines[6]) / pow(2, 10)) - 0.5) * 3.3) / 1100) * 1000
                if lines[7] == '':
                    eda = np.nan
                else:
                    eda = (((float(lines[7]) / pow(2, 10)) * 3.3) / 0.132) * pow(10, -6)
                file.write((str(round(ecg, 8)))+","+(str(round(eda, 10)))+"\n")

for f in glob.glob("/Users/olivia1/Library/Mobile Documents/com~apple~CloudDocs/ROBA TESI/DATI/PAZIENTI/ECG e EDA STRESSORS/*"):
    with open(f, "r") as csv_file:
        name = Path(f).stem
        csv_reader = csv.reader(csv_file, delimiter=detectDelimiter(f))

        with open("./data/stress/"+name+".txt", "w") as file:
            for lines in csv_reader:
                if lines[6] == '':
                    ecg = np.nan
                else:
                    ecg = ((((float(lines[6]) / pow(2, 10)) - 0.5) * 3.3) / 1100) * 1000
                if lines[7] == '':
                    eda = np.nan
                else:
                    eda = (((float(lines[7]) / pow(2, 10)) * 3.3) / 0.132) * pow(10, -6)
                file.write((str(round(ecg, 8)))+","+(str(round(eda, 10)))+"\n")
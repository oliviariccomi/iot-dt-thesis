import csv

### DA AGGIUSTARE

'''with open("/Users/olivia1/Desktop/ROBA TESI/DATI/FILE CSV GIUSTI/OFFICIAL/EDA/EDA_apnea_sa1_1min.csv", "r") as csv_file:
    arrx = []
    csv_reader = csv.reader(csv_file, delimiter='\t')'''

    # CHANGE NAME BASE ON PERSON
with open("eda/normal/try.txt", "r") as file:
    with open("eda/normal/correct/t.txt", "w") as f:
        lines = file.readlines()
        count=0
        for x in lines:
            a = x.strip()
            value = (((float(a) / pow(2, 10)) * 3.3) / 0.132) * pow(10, -6)
            count+=1
            f.write((str(round(value, 8)))+"\n")
    with open("eda/timeFiles/1.txt", "w") as f2:
        for i in range(count):
            f2.write(str(i+1)+"\n")
import csv
import sys

args = sys.argv[1:]

first_csv_input = args[0]
second_csv_input = args[1]

first_output_file = args[2]
second_output_file = args[3]

print("Input CSV-1: " + first_csv_input)
print("Input CSV-2: " + first_csv_input)
print("Output CSV-1: " + first_output_file)
print("Output CSV-2: " + second_output_file)

with open(first_csv_input, "r") as csv_file:
    arrx = []
    csv_reader = csv.reader(csv_file, delimiter='\t')

    # CHANGE NAME BASED ON PERSON
    with open(first_output_file, "w") as file:
        for lines in csv_reader:
            value = ((((float(lines[6]) / pow(2, 10)) - 0.5) * 3.3) / 1100) * 1000
            file.write((str(round(value, 8)))+"\n")

print("First execution block completed !")

with open(second_csv_input, "r") as csv_file:
    arrx = []
    csv_reader = csv.reader(csv_file, delimiter='\t')

    # CHANGE NAME BASE ON PERSON
    with open(second_output_file, "w") as file:
        for lines in csv_reader:
            value = ((((float(lines[6]) / pow(2, 10)) - 0.5) * 3.3) / 1100) * 1000
            file.write((str(round(value, 8)))+"\n")

print("Second execution block completed !")




This database contains the 166 Galvanic Skin Response (GSR) signal registers collected from the subjects participating in the first experiment (EXP 1) presented in:

R. Martinez, A. Salazar-Ramirez, A. Arruti, E. Irigoyen, J. I. Martin and J. Muguerza, "A Self-Paced Relaxation Response Detection System Based on Galvanic Skin Response Analysis," in IEEE Access, vol. 7, pp. 43730-43741, 2019. doi: 10.1109/ACCESS.2019.2908445

In addition to the files containing the GSR signals, for each subject there is an extra file. The labels given by the experts to the Relaxation Responses (RResp) can be found in these extra files, along with the central window time in which these labels were given.

* GSR signals of each participant:
The files whose names begin with letter "A" correspond to the GSR registers extracted from the participants. These files have a single column which correspond to the values of the GSR signal sampled at Fs=1Hz.

* Labels of each signal:
The files whose names begin with "LABEL" correspond to the labels of the RResp of each subject.
These files have two columns. The first column corresponds to the label of the register and the second column corresponds to the timestamp for that given label. The registers have been labeled using 20s windows (sliding every 5s) and being the labels positioned in the center of the window. For example:

-1 12.5  --> In the time window going from 2.5s to 22.5s, the RResp label corresponds to RResp=-1, being the  center of the window at 12.5s.

There are four RResp intensity levels: 0 stands for the absence of any RResp, -1 for a Low intensity RResp, -2 for a Medium intensity RResp and -3 for a High intensity RResp. 

#!/usr/bin/python

# ====================================================================================
#  Experiment 2 from the paper
#  "Population-based vs. Single-solution heuristics for the Travelling Thief Problem"
# ====================================================================================

import os
import time


def to_matrix(l, n):
	return [l[i:i+n] for i in xrange(0, len(l), n)]

path = 'experiments/eil51_sub' 
files = os.listdir(path)

files_ttp = [i for i in files if i.endswith('.ttp')]

files_ttp.sort()

group_size = 27

files_ttp = to_matrix(files_ttp, group_size)

repetition = 10
for k in xrange(repetition):
	for i in xrange(group_size):
		for instance in files_ttp:
			print(instance[i])
				
	 		os.system("sbatch -o " + instance[i] + "_" + str(k) + "_s1.out run_exp.sh "+ path + "/" + instance[i] +" S1")
	 		time.sleep(5)
	 	time.sleep(5)

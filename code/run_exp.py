#!/usr/bin/python

# ====================================================================================
#  Experiment 3 from the paper
#  "Population-based vs. Single-solution heuristics for the Travelling Thief Problem"
# ====================================================================================

import os
import time

instances = []
tours = []

folder = "./experiments/eil51_sub/"


for entry in os.listdir(folder):
	if entry.endswith("_01.ttp") or entry.endswith("_05.ttp") or entry.endswith("_10.ttp"):
		instances.append(entry)
	if entry.endswith("lkh.tour"):
		tours.append(entry)

# print(instances)
# print(tours)


# test all instances
for instance in instances:
	for tour in tours:
		command = "sbatch run_exp.sh " + folder + instance + " " + folder + tour
		print(command)
#     	os.system(command)
    	time.sleep(10)

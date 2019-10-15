#!/usr/bin/python

# ====================================================================================
#  
#  Run TTP-DP to the Travelling Thief Problem"
# ====================================================================================

import os
import time

instances = []

folder = "./experiments/eil51_sub/"


for entry in os.listdir(folder):
	if entry.endswith(".ttp"):
		instances.append(entry)

print('No. of files: ' + str(len(instances)))
instances = sorted(instances)
# print(instances)


# test all instances
for instance in instances:
	command = "sbatch run_exp.sh " + folder + instance 
#	print(command)
     	os.system(command)
    	time.sleep(60)

#!/bin/bash

#SBATCH --array=1-300
#SBATCH -p batch	   ###batch  or   highmem                       # partition (this is the queue your job will be added to) 
#SBATCH -N 1               	                                # number of nodes (due to the nature of sequential processing, here uses single node)
#SBATCH -n 16              	                                # number of cores
#SBATCH --time=3-00:00:00    	                                # time allocation, which has the format (D-HH:MM), here set to 3 days
#SBATCH --mem=8GB       # memory pool for all cores

###mkdir ./testSLURM.sh-clusterout 2>/dev/null;  # create directory and do not complain if the directory already exists
###SBATCH --output='testSLURM.sh-clusterout/slurm-%A_%a.out'

# Notification configuration 
#SBATCH --mail-type=END					    	# Type of email notifications will be sent (here set to END, which means an email will be sent when the job is done)
#SBATCH --mail-type=FAIL   					# Type of email notifications will be sent (here set to FAIL, which means an email will be sent when the job is fail to complete)
#SBATCH --mail-user=junhua.wu@adelaide.edu.au  		# Email to which notification will be sent

# Executing script (Example here is sequential script and you have to select suitable compiler for your case.)
#module load Java
#bash ./my_program.sh 	                                        # bash script used here for demonstration purpose, you should select proper compiler for your needs


offset=0
new_index=$((SLURM_ARRAY_TASK_ID + offset))

out=slurm-${SLURM_ARRAY_JOB_ID}_${SLURM_ARRAY_TASK_ID}.out
log=$(tail -n+$new_index $1 | head -n1 | cut -f 2 -d '>')

# THE FOLLOWING IS THE IMPORTANT LINE
$(tail -n+$new_index $1 | head -n1 | cut -f 1 -d '>')
mv ${out} ${log}_${SLURM_ARRAY_JOB_ID}_${SLURM_ARRAY_TASK_ID}.log


## HOWTO
## sbatch --array=1-300 slurm_submit_batch.sh eil76_n75_uncorr_01_lhv.array
## sbatch --array=1-300 slurm_submit_batch.sh eil76_n75_uncorr_01_lsc.array

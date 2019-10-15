#!/bin/bash
#SBATCH -p batch        # partition (this is the queue your job will be added to) 
#SBATCH -N 1            # number of nodes (here uses 2)
#SBATCH -n 1           # number of cores (here 64 cores requested)
#SBATCH --time=1-00:00 # time allocation, which has the format (D-HH:MM), here set to 1 hour
#SBATCH --mem=32GB      # memory pool for all cores (here set to 32 GB)


# #SBATCH --mail-type=END # Type of email notifications will be sent (here set to END, which means an email will be sent when the job is done)
# #SBATCH --mail-type=FAIL # Type of email notifications will be sent (here set to FAIL, which means an email will be sent when the job is fail to complete)
# #SBATCH --mail-user=junhua.wu@adelaide.edu.au # Email to which notification will be sent

java -cp "bin:lib/gson-2.3.1.jar" -Xmx4g nkp.experiment.LsExp $@

#!/bin/bash

for i in $(seq 1 300);
do
$(tail -n+$i $1 | head -n1 | cut -f 1 -d '>')
done



NOW=`date +"%Y%m%d-%H%M%S"`
OUTPUT=$(NOW).log

#ARGUMENTS=experiments/small/test1.conf
#ARGUMENTS=../../multiobj/experiments/eil76
MAINCLASS=ttp.moea.Heuristic
#MAINCLASS=pwt.experiment.multiobjective.FrontExp
#MAINCLASS=ttp.tour.TspGenerator
TESTCASE=pwt.heuristic.LocalSearchTests

run: compile
	java -cp "bin:lib/gson-2.3.1.jar" -Xmx16g $(MAINCLASS) $(ARGUMENTS) &> $(OUTPUT) &

run_test: compile_test 
	java -cp "bin:lib/gson-2.3.1.jar:lib/junit-4.12.jar:lib/hamcrest-core-1.3.jar" -Xmx8g org.junit.runner.JUnitCore $(TESTCASE) &> $(OUTPUT) &

compile_test: compile
	javac -sourcepath test -d bin -cp "bin:lib/junit-4.12.jar:lib/gson-2.3.1.jar" test/ttp/heuristic/*.java

compile:
	@javac -sourcepath src -d bin -cp "lib/gson-2.3.1.jar" src/pwt/experiment/*.java src/pwt/experiment/multiobjective/*.java src/ttp/tour/*.java src/ttp/moea/*.java


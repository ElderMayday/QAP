This is a README for the QAP program:

The program is written in Java 1.8.0

To compile the program you must run
./compile.sh

This may require authorizing the execution rights
chmod +x compile.sh

The output will be generated as "out/artifacts/qap_jar/qap.jar"

--------------------------------------------------------------------------------------------------------------------

You have three modes of exectution:

1) Experimental mode - runs 6 predefined configurations on the instances of folder "QAP_instances"
java -jar qap.jar --experiments

2) Convergence mode - runs 6 predefined configurations on the instance "chr15b.dat" with monitoring the solution qualities generated
java -jar qap.jar --convergence

3) Custom mode (used by irace) - runs the given configuration on the given instances
java -jar qap.jar [instance_name] [seed] [instance_path] {other arguments} --end

Example:
java -jar qap.jar testConfig instance 1234567 QAP_instances\chr15b.dat --local-search-idsia --eas --m 85 --rho 0.32 --rounds-reinitialize 8672 --q 0.42 --k 2.09 --Q 3.6 --sigma 54 --end

There are many arguments that describe the configuration:
[OPTIONAL] --best - orders to output the best values during the run
[OPTIONAL] --output [path] - to output the solution into a file
--local-search-none - none local search must be used
--local-search-idsia - the local search from IDSIA paper must be used
--eas - EAS must be used as the global update technique
--ras - RAS must be used as the global update technique
--m - number of ants
--rho - weakness of evaporation
--rounds-reinitialize - numbers of non-improving rounds in a row to diversify
--q - probability of using exploiting policy
--k - selection power
--Q - deposit factor
--sigma - number of depositing for Elitist Ant System
--w - number of depositing for Rank-based Ant System
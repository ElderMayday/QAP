rm -rf src/program/*.class
rm -rf src/program/localSearch/*.class
rm -rf src/program/solver/*.class

javac @sources.txt
jar cvfm out/artifacts/qap_jar/qap.jar Manifest.txt -C src/ .
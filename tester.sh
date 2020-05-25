
#!/usr/bin/env bash

javac src/*.java

w=5
m=100
a=5
h=10
echo "w, m, a, h, t1"
for i in {1..10}
do
   #A=$(expr $a + $i)
   printf "%s,%s,%s,%s," "$i" "$m" "$a" "$h"
   java src/Main $i $m $a $h
   echo ""
done

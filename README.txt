Program Notes

IDE USed: IntelliJ
Use provided IDE for recompiling or opening project files

Used Java 1.8.1 for build

Blocked IP's can be changed in the PacketHandler class at the top. Following requirements to change blocked IP's on recompile.

Source code found in SOURCE folder
Built Jar found in BUILD folder

Instructions:
1) Launch Jar File - program will listen for DNS packet
2) Make dig request using @localhost on port 2500
       Example: dig @localhost -p 2500 en.wikipedia.org
3) Response will be standard dig with IP replaced

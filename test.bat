call mvn clean package
@REM java -jar target\bin2png.jar custom.002 -start 13001 -zx -width 64 -height 192
@REM java -jar target\bin2png.jar custom.002 -start 18153 -zx -width 96 -height 48
java -jar target\bin2png.jar custom.002 -start 13001 -zxcolor -width 64 -height 192
java -jar target\bin2png.jar custom.002 -start 18153 -zxcolor -width 96 -height 48

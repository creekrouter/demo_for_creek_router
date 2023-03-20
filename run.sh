task=":app:assemble"
./gradlew clean $task
echo '---------------------- clean finished --------------------------'
./gradlew $task
echo '---------------------- build finished --------------------------'

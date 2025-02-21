make test:
	./gradlew test
	./gradlew checkstyleMain

make report:
	./gradlew jacocoTestReport
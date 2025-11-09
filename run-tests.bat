@echo off
echo ========================================
echo    EXECUTION DES TESTS JUNIT + JACOCO
echo ========================================
echo.

echo Nettoyage du projet...
call mvn clean

echo.
echo Compilation du projet...
call mvn compile

echo.
echo Execution des tests avec couverture JaCoCo...
call mvn test

echo.
echo Generation du rapport JaCoCo...
call mvn jacoco:report

echo.
echo ========================================
echo    TESTS TERMINES
echo ========================================
echo.
echo Rapports disponibles dans:
echo - target/site/jacoco/index.html (Couverture JaCoCo)
echo - target/surefire-reports/ (Rapports de tests)
echo.

pause

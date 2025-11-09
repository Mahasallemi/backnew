#!/bin/bash

echo "========================================"
echo "   EXECUTION DES TESTS JUNIT + JACOCO"
echo "========================================"
echo

echo "Nettoyage du projet..."
mvn clean

echo
echo "Compilation du projet..."
mvn compile

echo
echo "Execution des tests avec couverture JaCoCo..."
mvn test

echo
echo "Generation du rapport JaCoCo..."
mvn jacoco:report

echo
echo "========================================"
echo "   TESTS TERMINES"
echo "========================================"
echo
echo "Rapports disponibles dans:"
echo "- target/site/jacoco/index.html (Couverture JaCoCo)"
echo "- target/surefire-reports/ (Rapports de tests)"
echo

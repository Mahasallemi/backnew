-- Script SQL pour ajouter les colonnes manquantes à la table bon_de_travail
-- Exécuter ce script dans votre base de données MySQL

-- Ajouter la colonne intervention_id (clé étrangère vers demande_intervention)
ALTER TABLE bon_de_travail 
ADD COLUMN intervention_id BIGINT NULL,
ADD CONSTRAINT fk_bon_travail_intervention 
FOREIGN KEY (intervention_id) REFERENCES demande_intervention(id) 
ON DELETE SET NULL ON UPDATE CASCADE;

-- Ajouter la colonne testeur_code_gmao (clé étrangère vers testeur)
ALTER TABLE bon_de_travail 
ADD COLUMN testeur_code_gmao VARCHAR(255) NULL,
ADD CONSTRAINT fk_bon_travail_testeur 
FOREIGN KEY (testeur_code_gmao) REFERENCES testeur(code_gmao) 
ON DELETE SET NULL ON UPDATE CASCADE;

-- Vérifier la structure de la table après modification
DESCRIBE bon_de_travail;

-- Optionnel: Mettre à jour les bons de travail existants avec des données par défaut
-- UPDATE bon_de_travail SET intervention_id = NULL, testeur_code_gmao = NULL WHERE intervention_id IS NULL;

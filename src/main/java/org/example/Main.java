package org.example;

import org.example.utils.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.IOException;

public class Main {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        List<ClassHandler> classes = new ArrayList<>();

        while (true) {
            System.out.println("Choisissez un type :");
            System.out.print("1. Classe                 ");
            System.out.println("2. Interface");
            System.out.print("3. Classe Abstraite        ");
            System.out.println("4. Quitter");
            System.out.print("Votre choix (1-4) : ");

            int choice = getValidIntInput(1, 4);
            if (choice == 4) break;

            boolean isInterface = choice == 2;
            boolean isAbstract = choice == 3;

            System.out.print("Entrez le nom de la " +
                    (isInterface ? "Interface" :
                            isAbstract ? "Classe Abstraite" :
                                    "Classe") + " : ");
            String className = scanner.nextLine();

            ClassHandler classHandler = new ClassHandler(className, isInterface, isAbstract);
            classes.add(classHandler);


                System.out.print("Voulez-vous ajouter des attributs ? (y/n) : ");
                if (scanner.nextLine().toLowerCase().equals("y")) {
                    handleAttributes(classHandler);
            }

            // Gestion des méthodes
            System.out.print("Voulez-vous ajouter une méthode ? (y/n) : ");
            if (scanner.nextLine().toLowerCase().equals("y")) {
                handleMethods(classHandler);
            }

            System.out.print("Voulez-vous créer une autre classe ? (y/n) : ");
            if (!scanner.nextLine().toLowerCase().equals("y")) {
                break;
            }
        }

        // Gestion des relations
        handleRelations(classes);

        // Génération du diagramme final
        try {
            StringBuilder umlContent = new StringBuilder();
            for (ClassHandler cls : classes) {
                umlContent.append(cls.toUMLString()).append("\n\n");
            }
            Scanner scanner = new Scanner(System.in);
            String nameFile;
            do {
                System.out.print("Saisir un nom valide de votre diagrame : ");
                nameFile = scanner.nextLine();
            } while (nameFile.isEmpty());
            String nameFNotSpace = nameFile.replaceAll("\\s+", "");

            String imagePath = "src/main/resources/outputImage/" + nameFNotSpace + ".png";
            String pdfPath = "src/main/resources/outputPdf/" + nameFNotSpace + ".pdf";
                //si dossier pas exist
            new File("src/main/resources/outputImage").mkdirs();
            new File("src/main/resources/outputPdf").mkdirs();

            PlantUMLGenerator.generateUMLImage(umlContent.toString(), imagePath);
            JavaPDFGenerator.convertImageToPDF(imagePath, pdfPath);

            System.out.println("Diagramme généré avec succès !");
            System.out.println("Chemin du PDF généré : " + pdfPath);
        } catch (IOException e) {
            System.out.println("Erreur lors de la génération du diagramme : " + e.getMessage());
        }
    }

    private static void handleAttributes(ClassHandler classHandler) {
        System.out.println("Ajoutez des attributs à la classe (entrez 'done' lorsque vous avez terminé) :");

        while (true) {
            // Afficher les types disponibles
            System.out.println("Choisissez un type : ");
            for (int i = 0; i < DataType.values().length - 1; i++) {
                System.out.println((i + 1) + ". " + DataType.values()[i].getTypeName());
            }
            System.out.print("Votre choix pour le type de l'attribut (1-5 ou 'done') : ");

            String input = scanner.nextLine();
            if (input.toLowerCase().equals("done")) break;

            // Validation de l'entrée
            int typeChoice = -1;
            try {
                typeChoice = Integer.parseInt(input);
                if (typeChoice < 1 || typeChoice > 5) {
                    System.out.print("Veuillez entrer un nombre valide entre 1 et 5 : ");
                    continue; // Re-demander l'entrée si le choix est invalide
                }
            } catch (NumberFormatException e) {
                System.out.print("Veuillez entrer un nombre valide entre 1 et 5 :");
                continue; // Re-demander l'entrée si le format est invalide
            }

            DataType type = DataType.fromInt(typeChoice);  // Assurez-vous que DataType.fromInt existe et fonctionne

            System.out.print("Entrez le nom de l'attribut : ");
            String attrName = scanner.nextLine();

            AttributeVisibility visibility = getVisibility();

            classHandler.addAttribute(new AttributeHandler(attrName, type, visibility));
        }
    }

    private static void handleMethods(ClassHandler classHandler) {
        while (true) {
            System.out.print("Entrez le nom de la méthode (ou 'done' pour terminer) : ");
            String methodName = scanner.nextLine();
            if (methodName.toLowerCase().equals("done")) break;

            System.out.println("Choisissez le type de retour :");
            for (int i = 0; i < DataType.values().length; i++) {
                System.out.print ((i + 1) + ". " + DataType.values()[i].getTypeName()+ "  ");
            }
            System.out.println();
            System.out.print("=>  Type : ");

            int returnTypeChoice = getValidIntInput(1, 6);
            DataType returnType = DataType.fromInt(returnTypeChoice);

            AttributeVisibility visibility = getVisibility();

            MethodHandler method = new MethodHandler(methodName, returnType, visibility);

            // Gestion des paramètres
            while (true) {
                System.out.print("Voulez-vous ajouter un paramètre à la méthode ? (y/n) : ");
                if (!scanner.nextLine().toLowerCase().equals("y")) break;

                System.out.println("Choisissez un type pour le paramètre :");
                for (int i = 0; i < DataType.values().length - 1; i++) {
                    System.out.print((i + 1) + ". " + DataType.values()[i].getTypeName()+ " ");
                }
                System.out.println();
                System.out.print("=>  Type : ");
                int paramTypeChoice = getValidIntInput(1, 5);
                DataType paramType = DataType.fromInt(paramTypeChoice);

                System.out.print("Entrez le nom du paramètre : ");
                String paramName = scanner.nextLine();

                method.addParameter(paramName, paramType);
            }

            classHandler.addMethod(method);
        }
    }

    private static int getValidIntInput(int min, int max) {
        while (true) {
            try {
                String input = scanner.nextLine();
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException e) {
                // Continue to error message
            }
            System.out.println("Veuillez entrer un nombre valide entre " + min + " et " + max + " !");
        }
    }

    private static AttributeVisibility getVisibility() {
        System.out.print("Choisissez la visibilité (+, -, #) :  ");
        while (true) {
            String visibility = scanner.nextLine().trim();
            switch (visibility) {
                case "+":
                    return AttributeVisibility.PUBLIC;
                case "-":
                    return AttributeVisibility.PRIVATE;
                case "#":
                    return AttributeVisibility.PROTECTED;
                default:
                    System.out.println("Visibilité invalide. Veuillez choisir entre +, - ou # :");
            }
        }
    }

    private static void handleRelations(List<ClassHandler> classes) {
        while (true) {
            System.out.print("Voulez-vous ajouter une relation ? (y/n) : ");
            if (!scanner.nextLine().toLowerCase().equals("y")) break;

            System.out.println("\nClasses disponibles :");
            for (int i = 0; i < classes.size(); i++) {
                System.out.println((i + 1) + ". " + classes.get(i).getName());
            }

            System.out.print("Sélectionnez la première classe (1-" + classes.size() + ") : ");
            int sourceIndex = getValidIntInput(1, classes.size()) - 1;

            System.out.print("Sélectionnez la deuxième classe (1-" + classes.size() + ") : ");
            int targetIndex = getValidIntInput(1, classes.size()) - 1;

            System.out.println("\nTypes de relations disponibles :");
            RelationType[] types = RelationType.values();
            for (int i = 0; i < types.length; i++) {
                System.out.println((i + 1) + ". " + types[i].getDescription() +
                        " (" + types[i].getSymbol() + ")");
            }

            System.out.print("Sélectionnez le type de relation (1-" + types.length + ") : ");
            int typeIndex = getValidIntInput(1, types.length) - 1;
            RelationType selectedType = types[typeIndex];

            RelationHandler relation = new RelationHandler(
                    classes.get(sourceIndex).getName(),
                    classes.get(targetIndex).getName(),
                    selectedType
            );

            if (selectedType == RelationType.ASSOCIATION ||
                    selectedType == RelationType.AGGREGATION ||
                    selectedType == RelationType.COMPOSITION) {

                System.out.print("Cardinalité source (ex: 1, *, 0..1, 1..*) [Entrée pour ignorer] : ");
                String sourceCard = scanner.nextLine().trim();

                System.out.print("Cardinalité cible (ex: 1, *, 0..1, 1..*) [Entrée pour ignorer] : ");
                String targetCard = scanner.nextLine().trim();

                if (!sourceCard.isEmpty() || !targetCard.isEmpty()) {
                    relation.setCardinalities(sourceCard, targetCard);
                }
            }

            System.out.print("Ajouter un label à la relation ? [Entrée pour ignorer] : ");
            String label = scanner.nextLine().trim();
            if (!label.isEmpty()) {
                relation.setRelationLabel(label);
            }

            classes.get(sourceIndex).addRelation(relation);
        }
    }
}

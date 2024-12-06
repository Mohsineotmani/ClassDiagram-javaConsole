package org.example.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ClassHandler {
    private final String name;
    private final boolean isInterface;
    private final boolean isAbstract;
    private final List<AttributeHandler> attributes;
    private final List<MethodHandler> methods;
    private final List<RelationHandler> relations;

    public ClassHandler(String name, boolean isInterface, boolean isAbstract) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la classe ne peut pas être null ou vide");
        }

        this.name = name;
        this.isInterface = isInterface;
        this.isAbstract = isAbstract;
        this.attributes = new ArrayList<>();
        this.methods = new ArrayList<>();
        this.relations = new ArrayList<>();
    }

    // Getters
    public String getName() {
        return name;
    }

    public boolean isInterface() {
        return isInterface;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public List<AttributeHandler> getAttributes() {
        return Collections.unmodifiableList(attributes);
    }

    public List<MethodHandler> getMethods() {
        return Collections.unmodifiableList(methods);
    }

    public List<RelationHandler> getRelations() {
        return Collections.unmodifiableList(relations);
    }

    // Méthodes d'ajout avec validation
    public void addAttribute(AttributeHandler attribute) {
        if (attribute == null) {
            throw new IllegalArgumentException("L'attribut ne peut pas être null");
        }

        if (hasAttributeWithName(attribute.getName())) {
            throw new IllegalArgumentException("Un attribut avec le nom '" + attribute.getName() + "' existe déjà");
        }
        attributes.add(attribute);
    }

    public void addMethod(MethodHandler method) {
        if (method == null) {
            throw new IllegalArgumentException("La méthode ne peut pas être null");
        }
        if (hasMethodWithSignature(method)) {
            throw new IllegalArgumentException("Une méthode avec la même signature existe déjà");
        }
        methods.add(method);
    }

    public void addRelation(RelationHandler relation) {
        if (relation == null) {
            throw new IllegalArgumentException("La relation ne peut pas être null");
        }
        // Vérification que la classe source de la relation correspond bien au nom de la classe actuelle
        if (!relation.getSourceClass().trim().equalsIgnoreCase(name.trim())) {
            throw new IllegalArgumentException("La relation doit avoir cette classe comme source. Classe source: "
                    + relation.getSourceClass() + ", Classe attendue: " + name);
        }

        // Ajout de la relation
        relations.add(relation);
    }


    // Méthodes de recherche
    public Optional<AttributeHandler> findAttributeByName(String name) {
        return attributes.stream()
                .filter(attr -> attr.getName().equals(name))
                .findFirst();
    }

    public List<MethodHandler> findMethodsByName(String name) {
        return methods.stream()
                .filter(method -> method.getName().equals(name))
                .collect(Collectors.toList());
    }

    public List<RelationHandler> findRelationsWithClass(String targetClassName) {
        return relations.stream()
                .filter(rel -> rel.getTargetClass().equals(targetClassName))
                .collect(Collectors.toList());
    }

    // Méthodes de validation
    private boolean hasAttributeWithName(String name) {
        return attributes.stream().anyMatch(attr -> attr.getName().equals(name));
    }

    private boolean hasMethodWithSignature(MethodHandler newMethod) {
        return methods.stream().anyMatch(method ->
                method.getName().equals(newMethod.getName()) &&
                        method.getParameters().equals(newMethod.getParameters())
        );
    }

    // Méthode de génération UML
    public String toUMLString() {
        StringBuilder sb = new StringBuilder();

        // En-tête de la classe
        appendClassHeader(sb);

        // Corps de la classe
        sb.append(" {\n");

        // Attributs
        if (!attributes.isEmpty()) {
            appendAttributes(sb);
        }

        // Séparateur entre attributs et méthodes si nécessaire
        if (!attributes.isEmpty() && !methods.isEmpty()) {
            sb.append("\n");
        }

        // Méthodes
        if (!methods.isEmpty()) {
            appendMethods(sb);
        }

        // Fermeture de la classe
        sb.append("}");

        // Relations
        if (!relations.isEmpty()) {
            appendRelations(sb);
        }

        return sb.toString();
    }

    private void appendClassHeader(StringBuilder sb) {
        if (isInterface) {
            sb.append("interface ");
        } else {
            if (isAbstract) {
                sb.append("abstract ");
            }
            sb.append("class ");
        }
        sb.append(name);
    }

    private void appendAttributes(StringBuilder sb) {
        attributes.stream()
                .map(AttributeHandler::toUMLString)
                .forEach(attr -> sb.append("    ").append(attr).append("\n"));
    }

    private void appendMethods(StringBuilder sb) {
        methods.stream()
                .map(MethodHandler::toUMLString)
                .forEach(method -> sb.append("    ").append(method).append("\n"));
    }

    private void appendRelations(StringBuilder sb) {
        relations.forEach(relation ->
                sb.append("\n").append(relation.toUMLString()));
    }

    @Override
    public String toString() {
        return name + (isInterface ? " (Interface)" :
                isAbstract ? " (Classe Abstraite)" :
                        " (Classe)");
    }

}
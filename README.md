# JavaTga

- **Rayons primaires** avec ombrage Phong (ambiant, diffus, spéculaire)
- **Rays d'ombre** pour ombres
- **Réflexions** matériaux tel que le mirroir
- **Réfractions** matériaux transparents avec indice de réfraction
- **Primitives** : sphères, plans infinis, triangles, cubes
- **Parallelisme** via `ExecutorService` pour accélérer le rendu

Rendu des images

Les fichiers TGA de rendu sont déjà générés et disponibles dans le répertoire de sortie ; il suffit de les ouvrir avec un visualiseur TGA pour voir directement les scènes modélisées. (La génération du damier en triangles peut prendre plus de temps lors du calcul.)

Une extension sur InteliJ permet de visualiser directement.

## Structure du projet

```
src/fr/florian/
├── JavaTga.java       # Classe principale : construction de la scène, lancer de rayons, enregistrement TGA
├── Vec3f.java         # Vecteurs 3D et opérations (addition, produit scalaire, normalization…)
├── Material.java      # Propriétés de matériau (ambiant, diffus, spéculaire, etc.)
├── Geometry.java      # Classe abstraite (intersections, normales)
├── Sphere.java        # Primitive sphère
├── Plane.java         # Primitive plan infini
├── Triangle.java      # Primitive triangle
├── Light.java         # Source ponctuelle (ambiant, diffus, spéculaire)
└── README.md          # Ce fichier
```

## Prérequis

- Java 23 pour ma part
- Pas de dépendances externes (seulement le JDK)
- Sur InteliJ juste à ouvrir et creer un executeur avec comme classe principale JavaTGA.

### Scene personnalisée

Pour rendre uniquement une scène spécifique, éditez la méthode `main` de `JavaTga.java` :

```java
// Exemple pour ne générer que la scène « Damier + Réfraction »
public static void main(String[] args) throws IOException {
    renderScene(
      "damier_refraction.tga", w, h, blockSize,
      JavaTga::drawDamierRefractionScene
    );
}
```

## Ajouter une scène

1. **Définir des matériaux** :

   ```java
   Material rouge = new Material(
     new Vec3f(0.1f, 0f, 0f),    // ambiant
     new Vec3f(0.7f, 0f, 0f),    // diffus
     new Vec3f(1f, 1f, 1f),      // spéculaire
     32f,                        // shininess
     0f                          // réflectivité
   );
   ```

2. **Ajouter des objets** :

   ```java
   scene.add(new Sphere(
     new Vec3f(0f, 0.5f, -5f),
     0.5f,
     rouge
   ));

   scene.add(new Plane(
     new Vec3f(0f, -1f, 0f),
     new Vec3f(0f, 1f, 0f),
     rouge
   ));
   ```

3. **Ajouter des lumières** :

   ```java
   lights.add(new Light(
     new Vec3f(-4f, 3f, 0f),         // position
     new Vec3f(0.1f, 0.1f, 0.1f),    // ambiant
     new Vec3f(0.8f, 0.8f, 0.8f),    // diffus
     new Vec3f(1f, 1f, 1f)           // spéculaire
   ));
   ```

## Primitives disponibles

- **Sphere** : `new Sphere(center, radius, material)`
- **Plane** : `new Plane(point, normal, material)`
- **Triangle** : `new Triangle(A, B, C, material)`
- **Cube** : utilisez la fonction utilitaire pour générer 12 triangles :

  ```java
  addCubeAsTriangles(
    scene,
    new Vec3f(x, y, z),   // centre
    size,
    material
  );
  ```

## Paramètres de rendu

- **Résolution** : modifiez `w` et `h` dans `JavaTga.main`.
- **Distance du plan** de projection (`D`) contrôle le FOV.
- **Anti‐aliasing** : non implémenté (1 rayon/pixel).
- **Profondeur max.** pour réflexions/réfractions : `JavaTga.MAX_DEPTH`.

## Améliorations possibles

- Anti‐aliasing (suréchantillonnage)
- Textures UV
- Lampes directionnelles ou spot

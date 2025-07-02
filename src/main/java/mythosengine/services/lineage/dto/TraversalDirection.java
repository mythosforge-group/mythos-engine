package mythosengine.services.lineage.dto;

public enum TraversalDirection {
    /**
     * Navega "para cima", seguindo as relações do alvo para a fonte.
     * Útil para encontrar ancestrais, chefes, mestres, etc.
     */
    UPSTREAM,
    /**
     * Navega "para baixo", seguindo as relações da fonte para o alvo.
     * Útil para encontrar descendentes, subordinados, aprendizes, etc.
     */
    DOWNSTREAM
}
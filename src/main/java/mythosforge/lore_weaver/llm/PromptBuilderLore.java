package mythosforge.lore_weaver.llm;





public class PromptBuilderLore {

    public static String buildLoreConnectionPrompt(String articleName, String articleContent) {
        return String.format("""
        Aja como um mestre de worldbuilding criativo e um assistente de escrita.
        Com base no artigo de lore sobre "%s", cujo conteúdo é:
        ---
        %s
        ---
        
        Sugira entre 3 a 5 novas entidades (personagens, locais, facções, eventos históricos, etc.) que estejam diretamente relacionadas a este artigo.
        Para cada sugestão, forneça um nome, um tipo, um breve resumo e o tipo de relação com o artigo original.

        A resposta DEVE ser um array JSON válido e nada mais. Siga estritamente o formato abaixo:

        [
          {
            "nome": "Nome da Entidade Sugerida 1",
            "tipo": "TIPO_DA_ENTIDADE (ex: FACCAO, PERSONAGEM, LOCAL)",
            "resumo": "Uma breve descrição da entidade sugerida e sua conexão com o artigo original.",
            "tipo_relacao": "TIPO_DA_RELAÇÃO (ex: FUNDADA_EM, LIDERADO_POR, INIMIGO_DE)"
          },
          {
            "nome": "Nome da Entidade Sugerida 2",
            "tipo": "TIPO_DA_ENTIDADE (ex: EVENTO_HISTORICO)",
            "resumo": "Uma breve descrição do evento e sua importância.",
            "tipo_relacao": "ACONTECEU_EM"
          }
        ]
        """,
                articleName,
                articleContent
        );
    }
}
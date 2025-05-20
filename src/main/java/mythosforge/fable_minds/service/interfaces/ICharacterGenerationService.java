package mythosforge.fable_minds.service.interfaces;

import mythosforge.fable_minds.models.CharacterDnd;

public interface ICharacterGenerationService {
    String gerarFicha(Long campaignId, Long raceId, Long classId);
    CharacterDnd gerarFichaESalvar(Long campaignId, Long raceId, Long classId);
}
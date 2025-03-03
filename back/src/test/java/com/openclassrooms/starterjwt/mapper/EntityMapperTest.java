package com.openclassrooms.starterjwt.mapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.Mapper;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;

@Mapper(componentModel = "spring")
class EntityMapperTest {

    private TestEntityMapper mapper;
    
    @BeforeEach
    void setUp() {
        mapper = new TestEntityMapper();
    }

    @Test
    void toEntity_shouldConvertDtoToEntity() {
        // Given
        SessionDto dto = new SessionDto();
        dto.setCreatedAt(LocalDateTime.now());
        
        // When
        Session entity = mapper.toEntity(dto);
        
        // Then
        assertNotNull(entity);
        assertEquals(dto.getCreatedAt(), entity.getCreatedAt());
    }

    @Test
    void toDto_shouldConvertEntityToDto() {
        // Given
        Session entity = new Session();
        entity.setCreatedAt(LocalDateTime.now());
        
        // When
        SessionDto dto = mapper.toDto(entity);
        
        // Then
        assertNotNull(dto);
        assertEquals(entity.getCreatedAt(), dto.getCreatedAt());
    }

    @Test
    void toEntity_shouldConvertDtoListToEntityList() {
        // Given
        SessionDto dto1 = new SessionDto();
        SessionDto dto2 = new SessionDto();
        List<SessionDto> dtoList = Arrays.asList(dto1, dto2);
        
        // When
        List<Session> entityList = mapper.toEntity(dtoList);
        
        // Then
        assertNotNull(entityList);
        assertEquals(dtoList.size(), entityList.size());
    }

    @Test
    void toDto_shouldConvertEntityListToDtoList() {
        // Given
        Session entity1 = new Session();
        Session entity2 = new Session();
        List<Session> entityList = Arrays.asList(entity1, entity2);
        
        // When
        List<SessionDto> dtoList = mapper.toDto(entityList);
        
        // Then
        assertNotNull(dtoList);
        assertEquals(entityList.size(), dtoList.size());
    }

    // Test implementation of EntityMapper
    private class TestEntityMapper implements EntityMapper<SessionDto, Session> {
        @Override
        public Session toEntity(SessionDto dto) {
            if (dto == null) return null;
            Session entity = new Session();
            entity.setCreatedAt(dto.getCreatedAt());
            return entity;
        }

        @Override
        public SessionDto toDto(Session entity) {
            if (entity == null) return null;
            SessionDto dto = new SessionDto();
            dto.setCreatedAt(entity.getCreatedAt());
            return dto;
        }

        @Override
        public List<Session> toEntity(List<SessionDto> dtoList) {
            return dtoList.stream()
                .map(this::toEntity)
                .collect(java.util.stream.Collectors.toList());
        }

        @Override
        public List<SessionDto> toDto(List<Session> entityList) {
            return entityList.stream()
                .map(this::toDto)
                .collect(java.util.stream.Collectors.toList());
        }
    }
}

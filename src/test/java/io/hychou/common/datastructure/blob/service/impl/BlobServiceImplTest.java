package io.hychou.common.datastructure.blob.service.impl;

import io.hychou.common.datastructure.blob.dao.BlobRepository;
import io.hychou.common.datastructure.blob.entity.BlobEntity;
import io.hychou.common.datastructure.blob.service.BlobService;
import io.hychou.common.exception.service.ServiceException;
import io.hychou.common.exception.service.client.ElementNotExistException;
import io.hychou.common.exception.service.client.NullParameterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static io.hychou.test.common.util.AssertUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
public class BlobServiceImplTest<BLOB extends BlobEntity, INFO> {
    private BlobService<BLOB, INFO> blobServiceImpl;
    @Mock
    private BlobRepository<BLOB, INFO> blobRepository;

    @BeforeEach
    void setUp() {
        blobServiceImpl = spy(new BlobServiceImpl<>(blobRepository));
    }

    @Nested
    class listInfo {
        @Mock
        private List<INFO> blobInfoList;

        @Test
        @DisplayName("should return correct blob info list")
        void positiveBehavior() {
            when(blobRepository.findBlobInfoBy()).thenReturn(blobInfoList);
            assertEquals(blobInfoList, blobServiceImpl.listInfo());
        }
    }

    @Nested
    class readInfoById {

        private Long anyValidId = createAnyLong();
        private Long anyBadId = createAnyLong();
        @Mock
        private INFO blobInfo;

        @Test
        @DisplayName("should return correct blobInfo given valid id")
        void positiveBehavior() throws ServiceException {
            when(blobRepository.findBlobInfoById(anyValidId)).thenReturn(Optional.of(blobInfo));
            assertEquals(blobInfo, blobServiceImpl.readInfoById(anyValidId));
        }

        @Test
        @DisplayName("should throw element not found exception given bad id")
        void badInputProtect() {
            when(blobRepository.findBlobInfoById(anyBadId)).thenReturn(Optional.empty());
            assertThrows(ElementNotExistException.class, () -> blobServiceImpl.readInfoById(anyBadId));
        }

        @Test
        @DisplayName("should throw null exception given null id")
        void nullProtect() {
            assertThrows(NullParameterException.class, () -> blobServiceImpl.readInfoById(null));
        }
    }

    @Nested
    class readById {
        @Mock
        private BLOB blob;
        private Long anyValidId = createAnyLong();
        private Long anyBadId = createAnyLong();

        @Test
        @DisplayName("should return correct blob given valid id")
        void positiveBehavior() throws ServiceException {
            when(blobRepository.findById(anyValidId)).thenReturn(Optional.of(blob));
            assertEquals(blob, blobServiceImpl.readById(anyValidId));
        }

        @Test
        @DisplayName("should throw element not found exception given bad id")
        void badInputProtect() {
            assertThrows(ElementNotExistException.class, () -> blobServiceImpl.readById(anyBadId));
        }

        @Test
        @DisplayName("should throw null exception given null id")
        void nullProtect() {
            when(blobRepository.findById(anyBadId)).thenReturn(Optional.empty());
            assertThrows(NullParameterException.class, () -> blobServiceImpl.readById(null));
        }
    }

    @Nested
    class readInfoByName {
        @Mock
        private List<INFO> blobInfoList;
        private String anyValidName = createAnyString();

        @Test
        @DisplayName("should return correct blob info list given valid name")
        void positiveBehavior() throws ServiceException {
            when(blobRepository.findBlobInfoByName(anyValidName)).thenReturn(blobInfoList);
            assertEquals(blobInfoList, blobServiceImpl.readInfoByName(anyValidName));
        }

        @Test
        @DisplayName("should throw null exception given null name")
        void nullProtect() {
            assertThrows(NullParameterException.class, () -> blobServiceImpl.readInfoByName(null));
        }
    }

    @Nested
    class readByName {
        @Mock
        private List<BLOB> blobList;
        private String anyValidName = createAnyString();

        @Test
        @DisplayName("should return correct blob list given valid name")
        void positiveBehavior() throws ServiceException {
            when(blobRepository.findByName(anyValidName)).thenReturn(blobList);
            assertEquals(blobList, blobServiceImpl.readByName(anyValidName));
        }

        @Test
        @DisplayName("should throw null exception given null name")
        void nullProtect() {
            assertThrows(NullParameterException.class, () -> blobServiceImpl.readByName(null));
        }
    }

    @Nested
    class create {
        @Mock
        private BLOB blob;
        @Mock
        private BLOB savedBlob;

        @BeforeEach
        void setUp() {
            when(blob.getName()).thenReturn("a name");
            when(blob.getBlobBytes()).thenReturn(createAnyBytes());
        }

        @Test
        @DisplayName("should create the blob and return the saved blob given valid blob")
        void positiveBehavior() {
            when(blobRepository.save(blob)).thenReturn(savedBlob);

            assertAll(
                    () -> assertEquals(savedBlob, blobServiceImpl.create(blob)),
                    () -> verify(blobRepository, times(1)).save(blob)
            );
        }

        @Nested
        @DisplayName("should throw null exception")
        class nullProtect {
            @Test
            @DisplayName("given null blob")
            void nullBlob() {
                assertAll(
                        () -> assertThrows(NullParameterException.class, () -> blobServiceImpl.create(null)),
                        () -> verify(blobRepository, times(0)).save(any())
                );
            }

            @Test
            @DisplayName("given blob with null name")
            void nullName() {
                when(blob.getName()).thenReturn(null);
                assertAll(
                        () -> assertThrows(NullParameterException.class, () -> blobServiceImpl.create(blob)),
                        () -> verify(blobRepository, times(0)).save(any())
                );
            }

            @Test
            @DisplayName("given blob with null byte array")
            void nullByteArray() {
                when(blob.getBlobBytes()).thenReturn(null);
                assertAll(
                        () -> assertThrows(NullParameterException.class, () -> blobServiceImpl.create(blob)),
                        () -> verify(blobRepository, times(0)).save(any())
                );
            }

            @Test
            @DisplayName("given blob with both name id and null byte array")
            void nullNameAndNullByteArray() {
                when(blob.getName()).thenReturn(null);
                when(blob.getBlobBytes()).thenReturn(null);
                assertAll(
                        () -> assertThrows(NullParameterException.class, () -> blobServiceImpl.create(blob)),
                        () -> verify(blobRepository, times(0)).save(any())
                );
            }
        }
    }

    @Nested
    class updateById {
        @Mock
        private BLOB blob;
        @Mock
        private BLOB savedBlob;

        private Long anyValidId = createAnyLong();
        private Long anyBadId = createAnyLong();
        private byte[] anyValidBytes = createAnyBytes();

        @BeforeEach
        void setUp() throws ServiceException {
            doReturn(blob).when(blobServiceImpl).readById(anyValidId);
            when(blobRepository.save(blob)).thenReturn(savedBlob);
            doThrow(ElementNotExistException.class).when(blobServiceImpl).readById(anyBadId);
        }

        @Test
        @DisplayName("should update the blob return saved blob")
        void positiveBehavior() {

            assertAll(
                    () -> assertEquals(savedBlob, blobServiceImpl.updateById(anyValidId, anyValidBytes)),
                    () -> verify(blob, times(1)).setBlobBytes(anyValidBytes),
                    () -> verify(blobRepository, times(1)).save(blob)
            );
        }

        @Test
        @DisplayName("should throw element not found exception if id not exist")
        void badInputProtect() {
            assertAll(
                    () -> assertThrows(ElementNotExistException.class, () -> blobServiceImpl.updateById(anyBadId, anyValidBytes)),
                    () -> verify(blobRepository, times(0)).save(any())
            );
        }

        @Nested
        @DisplayName("should throw null exception")
        class nullProtect {
            @Test
            @DisplayName("given null id")
            void nullId() {
                assertAll(
                        () -> assertThrows(NullParameterException.class, () -> blobServiceImpl.updateById(null, anyValidBytes)),
                        () -> verify(blobRepository, times(0)).save(any())
                );
            }

            @Test
            @DisplayName("given null bytes")
            void nullBytes() {
                assertAll(
                        () -> assertThrows(NullParameterException.class, () -> blobServiceImpl.updateById(anyValidId, null)),
                        () -> verify(blobRepository, times(0)).save(any())
                );
            }

            @Test
            @DisplayName("given null id and null bytes")
            void nullIdAndNullBytes() {
                assertAll(
                        () -> assertThrows(NullParameterException.class, () -> blobServiceImpl.updateById(null, null)),
                        () -> verify(blobRepository, times(0)).save(any())
                );
            }
        }
    }

    @Nested
    class deleteById {

        private Long anyValidId = createAnyLong();
        private Long anyBadId = createAnyLong();

        @BeforeEach
        void setUp() {
            when(blobRepository.existsById(anyValidId)).thenReturn(true);
            when(blobRepository.existsById(anyBadId)).thenReturn(false);
        }

        @Test
        @DisplayName("should delete the blob given valid id")
        void positiveBehavior() throws ServiceException {
            blobServiceImpl.deleteById(anyValidId);
            verify(blobRepository, times(1)).deleteById(anyValidId);
        }

        @Test
        @DisplayName("should throw null exception given null id")
        void nullProtect() {
            assertThrows(NullParameterException.class, () -> blobServiceImpl.deleteById(null));
        }

        @Test
        @DisplayName("should throw element not exist exception given bad id")
        void badInputProtect() {
            assertAll(
                    () -> assertThrows(ElementNotExistException.class, () -> blobServiceImpl.deleteById(anyBadId)),
                    () -> verify(blobRepository, times(0)).deleteById(anyBadId)
            );
        }
    }
}
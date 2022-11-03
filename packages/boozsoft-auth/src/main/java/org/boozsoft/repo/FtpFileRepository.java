package org.boozsoft.repo;

import org.boozsoft.domain.entity.FtpFile;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FtpFileRepository extends ReactiveCrudRepository<FtpFile, String> {
    Mono<Long> countByOldName(String oldName);
    Mono<Long> countByHash(String hash);
    Mono<FtpFile> findByOldName(String oldName);
    Mono<FtpFile> findByNewName(String oldName);

    @Query("select * from ftp_file ff left join _app_group_sys_task_file f on f.file_id = ff.id where f.message_id = :messageId ")
    Flux<FtpFile> findByMessageId(String messageId);
}


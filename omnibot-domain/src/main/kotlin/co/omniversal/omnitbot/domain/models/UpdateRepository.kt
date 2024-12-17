package co.omniversal.omnitbot.domain.models

import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.Repository

@NoRepositoryBean
interface UpdateRepository<T, ID> : Repository<T, ID> {
    suspend fun findById(id: ID): T?

    suspend fun <S : T> update(entity: S): S
}
package com.example.notificationboleto.data.local.dao

import androidx.room.*
import com.example.notificationboleto.data.local.entity.BoletoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BoletoDao {

    @Query("SELECT * FROM boletos ORDER BY vencimento ASC")
    fun getBoletos(): Flow<List<BoletoEntity>>

    @Query("SELECT * FROM boletos")
    suspend fun getAllBoletos(): List<BoletoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(boleto: BoletoEntity)

    @Update
    suspend fun update(boleto: BoletoEntity)

    @Delete
    suspend fun delete(boleto: BoletoEntity)
}

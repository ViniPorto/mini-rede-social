package com.univille.mini_rede_social.amizades.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.univille.mini_rede_social.amizades.models.Amizade;
import com.univille.mini_rede_social.login.models.Usuario;

public interface AmizadeRepository extends JpaRepository<Amizade, Long> {

    @Query(value = """
            SELECT a1.usuarioAmigo FROM Amizade a1
            WHERE a1.usuarioPrincipal.codigo IN (SELECT a2.usuarioAmigo.codigo FROM Amizade a2 WHERE a2.usuarioPrincipal.codigo = :usuarioCodigo)
            AND a1.usuarioAmigo.codigo NOT IN (SELECT a3.usuarioAmigo.codigo FROM Amizade a3 WHERE a3.usuarioPrincipal.codigo = :usuarioCodigo)
            AND a1.usuarioAmigo.codigo != :usuarioCodigo
            ORDER BY FUNCTION('RAND')
            """, nativeQuery = false)
    List<Usuario> listarAmigosDeAmigosAleatorios(@Param("usuarioCodigo") Long usuarioCodigo, Pageable pageable);

    @Query(value = """
            SELECT u FROM Usuario u
            WHERE 
                (6371 * ACOS(
                    COS(RADIANS(:latitude)) 
                    * COS(RADIANS(u.latitude)) 
                    * COS(RADIANS(u.longitude) - RADIANS(:longitude)) 
                    + SIN(RADIANS(:latitude)) 
                    * SIN(RADIANS(u.latitude))
                )) <= :raio
            AND u.codigo <> :usuarioCodigo
            AND u.codigo NOT IN (SELECT a.usuarioAmigo.codigo FROM Amizade a WHERE a.usuarioPrincipal.codigo = :usuarioCodigo)
            ORDER BY FUNCTION('RAND')
            """, nativeQuery = false)
    List<Usuario> listarPessoasProximasAleatorias(@Param("raio") Integer raio, @Param("latitude") Double latitude, @Param("longitude") Double longitude, Pageable pageable, @Param("usuarioCodigo") Long usuarioCodigo);

    boolean existsByUsuarioPrincipalAndUsuarioAmigo(Usuario usuarioDestinatario, Usuario usuarioRemetente);

    @Query("""
            SELECT u FROM Amizade a
            JOIN a.usuarioAmigo u
            WHERE a.usuarioPrincipal.codigo = :usuarioCodigo
            """)
    Page<Usuario> listarTodasAmizades(Pageable pageable, @Param("usuarioCodigo") Long usuarioCodigo);
    
}

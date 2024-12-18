CREATE TABLE T_POSTAGEM_CURTIDA (
    PCU_CODIGO BIGINT NOT NULL IDENTITY(1,1),
    POS_CODIGO BIGINT NOT NULL,
    USU_CODIGO BIGINT NOT NULL,

    PRIMARY KEY(PCU_CODIGO),
    FOREIGN KEY (POS_CODIGO) REFERENCES T_POSTAGEM(POS_CODIGO),
    FOREIGN KEY (USU_CODIGO) REFERENCES T_USUARIO(USU_CODIGO)
);

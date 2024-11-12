CREATE TABLE T_NOTIFICACAO (
    NOT_CODIGO BIGINT NOT NULL IDENTITY(1,1),
    USU_CODIGO BIGINT NOT NULL,
    NOT_FOTO NVARCHAR(MAX) NOT NULL,
    NOT_DATA_CRIACAO DATETIME NOT NULL,
    NOT_DESCRICAO VARCHAR(255) NOT NULL,
    NOT_LIDA BIT NOT NULL,

    PRIMARY KEY(NOT_CODIGO),
    FOREIGN KEY (USU_CODIGO) REFERENCES T_USUARIO(USU_CODIGO)
);
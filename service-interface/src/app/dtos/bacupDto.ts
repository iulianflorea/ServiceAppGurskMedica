export class BackupDto{

  id?: number;
  sqlPath?: string;
  documentPath?: string;

  constructor(id: number, sqlPath: string, documentPath: string) {
    this.id = id;
    this.sqlPath = sqlPath;
    this.documentPath = documentPath;
  }
}

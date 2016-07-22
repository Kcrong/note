#include <stdio.h>
#include <stdlib.h>
#include <arpa/inet.h>

const unsigned char EXIF_SIGN[] = { 0xFF, 0xD8, 0xFF, 0xE1 };

struct IFD_Field {
	unsigned short tagID;
	unsigned short typeID;
	unsigned int countOfComponents;
	unsigned int value;
};

struct GEO_TAG {
	int deg, deg_div;
	int min, min_div;
	int sec, sec_div;
};

int main() {
	char buf[1024];
	unsigned char in[1024];

	printf("JPG file path : ");
	scanf("%1024s", buf);

	FILE *fp = fopen(buf, "r");
	if (fp == NULL) {
		printf("[*] invalid file ...\n");
		return -1;
	}

	fread(in, 1, 4, fp);

	for (int i = 0; i < 4; ++i) {
		if (in[i] != EXIF_SIGN[i]) {
			printf("[*] non-Exif format file ...\n");
			return -1;
		}
	}
	fseek(fp, 8, SEEK_CUR);

	unsigned short id;
	fread(&id, sizeof(short), 2, fp);
	if (id != 0x4D4D) {
		printf("[*] Couldn't analyse Little endian file ...\n");
		return -1;
	}

	unsigned int offset;
	fread(&offset, sizeof(int), 1, fp);
	offset = ntohl(offset);
	printf("[*] Exif Header offset : %d\n", offset);

	unsigned short fieldCount;
	fread(&fieldCount, sizeof(short), 1, fp);
	fieldCount = ntohs(fieldCount);
	printf("[*] Number of 0th IFD Field Count : %hd\n", fieldCount);

	IFD_Field gpsField;
	do {
		fread(&gpsField, sizeof(IFD_Field), 1, fp);
		gpsField.tagID = ntohs(gpsField.tagID);
	} while (gpsField.tagID != 0x8825);
	gpsField.value = ntohl(gpsField.value);
	printf("[*] GPS offset : %d\n", gpsField.value);

	fseek(fp, 12 + gpsField.value, SEEK_SET);

	fread(&fieldCount, sizeof(short), 1, fp);
	fieldCount = ntohs(fieldCount);
	printf("[*] Number of GPS IFD Field Count : %hd\n", fieldCount);
	if (fieldCount <= 2) {
		printf("[*] It has only altitude data\n");
		return -1;
	}

	fseek(fp, sizeof(IFD_Field) * fieldCount + 4, SEEK_CUR);

	for (int i = 0; i < 2; ++i) {
		GEO_TAG temp;
		fread(&temp, 24, 1, fp);

		double deg = (double)temp.deg / temp.deg_div;
		double min = (double)temp.min / temp.min_div;

		temp.sec = ntohl(temp.sec);
		temp.sec_div = ntohl(temp.sec_div);
		double sec = (double)temp.sec / temp.sec_div;

		printf("%.02f`%.02f\"%.02f\'\n", deg, min, sec);
	}

	return 0;
}

#include <stdio.h>

/* Table of CRCs of all 8-bit messages. */
unsigned long crc_table[256];

/* Flag: has the table been computed? Initially false. */
int crc_table_computed = 0;

/* Make the table for a fast CRC. */
void make_crc_table(void) {
  unsigned long c;
  int n, k;

  for (n = 0; n < 256; n++) {
    c = (unsigned long) n;
    for (k = 0; k < 8; k++) {
      if (c & 1)
        c = 0xedb88320L ^ (c >> 1);
      else
        c = c >> 1;
    }
    crc_table[n] = c;
  }
  crc_table_computed = 1;
}

   /* Update a running CRC with the bytes buf[0..len-1]--the CRC
      should be initialized to all 1's, and the transmitted value
      is the 1's complement of the final running CRC (see the
      crc() routine below)). */

unsigned long update_crc(unsigned long crc, unsigned char *buf, int len) {
  unsigned long c = crc;
  int n;

  if (!crc_table_computed)
    make_crc_table();
  for (n = 0; n < len; n++) {
    c = crc_table[(c ^ buf[n]) & 0xff] ^ (c >> 8);
  }
  return c;
}

   /* Return the CRC of the bytes buf[0..len-1]. */
unsigned long crc(unsigned char *buf, int len) {
  return update_crc(0xffffffffL, buf, len) ^ 0xffffffffL;
}

int main () {
  unsigned char buf[] = { 0x74, 0x45, 0x58, 0x74, 0xFF, 0xFF, 0xFF, 0xFF, 0x3A, 0x63, 0x72, 0x65, 0x61, 0x74, 0x65, 0x00, 0x32, 0x30, 0x31, 0x36, 0x2D, 0x30, 0x37, 0x2D, 0x32, 0x32, 0x54, 0x31, 0x32, 0x3A, 0x33, 0x35, 0x3A, 0x30, 0x38, 0x2B, 0x30, 0x32, 0x3A, 0x30, 0x30 };
  printf("%08x\n", crc(buf, sizeof(buf)));
}

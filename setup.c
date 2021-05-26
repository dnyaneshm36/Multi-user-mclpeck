#include<pbc/pbc.h>
#include<stdio.h>
#include<string.h>
#include<stdlib.h>
#include<gmp.h>
#include <openssl/ssl.h>
#include <openssl/sha.h>
#include <openssl/evp.h>



#define m 3
#define n 3
#define HASH_LEN 64
#define number_of_words 3

typedef struct TrapDoor{
	int n_words;
    element_t I[number_of_words];
    char *words[number_of_words];
    element_t Tj1;
    element_t Tj2;
    element_t Tj3;
}Q;
Q Q_rec[m];
element_t k;
pairing_t pairing;

typedef struct params
{
	element_t g,gpub;
}GP;
GP gp;


typedef struct sec_key
{
	element_t x,d;
}SK;

typedef struct public_key
{
	element_t y,t;
}PK;

typedef struct reciever
{
	SK sk[m];
	PK pk[m];
	element_t ID[m];
}Rec;
Rec Rc;

typedef struct CipherText
{
	element_t A;
	element_t B[m];
	element_t C[n];
}CT;
CT ct;

void Setup(element_t temp_setup)
{
	element_t s;
	element_init_Zr(k, pairing);
	element_set(k,temp_setup);
	element_init_Zr(s,pairing);
	element_random(s);
	element_printf("Master key s = %B\n",s);
	printf("---------------------\n");
	element_init_G1(gp.gpub,pairing);
	element_pow_zn(gp.gpub, gp.g, s);
	element_printf("gpub = %B\n",gp.gpub);
	printf("---------------------\n");
}

// {0, 1}∗ × G1 → Zq*  (id is assumed to be Zq*)
void h0(element_t id, element_t ti, element_t result){

	int len_a = element_length_in_bytes(id);
	char *str_a = (char *)malloc(len_a*sizeof(char));
	element_to_bytes(str_a, id);

	int len_b = element_length_in_bytes(ti);
	char *str_b = (char *)malloc(len_b*sizeof(char));
	element_to_bytes(str_b, ti);

	char hash[HASH_LEN];
	PKCS5_PBKDF2_HMAC_SHA1(str_a, len_a, str_b, len_b, 10, HASH_LEN, hash);

	element_from_hash(result, hash, HASH_LEN);
}

// {0,1}* -> G1
void H1(char *word, element_t result){
	char salt[20] = "H1_salt_value";
	char hash[HASH_LEN];
	PKCS5_PBKDF2_HMAC_SHA1(word, strlen(word), salt, strlen(salt), 1, HASH_LEN, hash);

	element_from_hash(result, hash, HASH_LEN);
}

// {0,1}* -> G1
void H2(char *word, element_t result){
	char salt[20] = "H2_salt_value";
	char hash[HASH_LEN];
	PKCS5_PBKDF2_HMAC_SHA1(word, strlen(word), salt, strlen(salt), 1, HASH_LEN, hash);

	element_from_hash(result, hash, HASH_LEN);
}

// ℎ3 ∶ {0, 1}∗ × G1 × G1 × G1 → Zq*
void h3(element_t id, element_t gpub, element_t yj, element_t tj, element_t result){
	element_t res1, res2;
	element_init_Zr(res1, pairing);
	element_init_Zr(res2, pairing);

	h0(id, gpub, res1);
	h0(yj, tj, res2);
	h0(res1, res2, result);

}

void Extract_Partial_Private_Key(element_t s)
{
	long int i;
	mpz_t temp,temp_res,temp_q;
	mpz_init(temp);
	mpz_init(temp_res);
	mpz_init(temp_q);
	element_t ti,alpha,mult,add,di;
	element_init_G1(ti,pairing);
	element_init_Zr(di,pairing);
	element_init_Zr(alpha,pairing);
	element_init_Zr(mult,pairing);
	element_init_Zr(add,pairing);
	for(i=0;i<m;i++)
	{
		element_t si;
		element_init_Zr(si,pairing);
		element_random(si);
		element_pow_zn(ti, gp.g, si);         
		h0(Rc.ID[i],ti,alpha);
		element_mul(mult, s, alpha);
		element_add(add, si, mult);    
		element_set(di,add);   
		element_init_Zr(Rc.sk[i].d,pairing);
		element_set(Rc.sk[i].d,di);
		element_printf("SK d for reciever %d = %B\n",i,Rc.sk[i].d);
		element_init_G1(Rc.pk[i].t,pairing);
		element_set(Rc.pk[i].t,ti);
		element_printf("PK t for reciever %d = %B\n",i,Rc.pk[i].t);
	}
}

void Set_Secret_Value()
{
	long int i;
	for(i=0;i<m;i++)
	{
		element_t xi;
		element_init_Zr(xi,pairing);
		element_random(xi);
		element_init_Zr(Rc.sk[i].x,pairing);
		element_set(Rc.sk[i].x,xi);
		element_printf("SK x for reciever %d = %B\n",i,Rc.sk[i].x);
	}
}

void Set_Public_Key()
{
	long int i;
	element_t yi;
	element_init_G1(yi,pairing);
	for(i=0;i<m;i++)
	{
		element_pow_zn(yi, gp.g, Rc.sk[i].x);
		element_init_G1(Rc.pk[i].y,pairing);
		element_set(Rc.pk[i].y,yi);
		element_printf("PK y for reciever %d = %B\n",i,Rc.pk[i].y);
	}
}

void mCLPECK(char *word[])
{
	element_t r,rdash,beta_j,temp_res,hi,fi,Ci,Bj;
	element_t temp_mult,temp_mult1,temp_pow1,temp_pow2;
	element_init_Zr(r,pairing);
	element_random(r);
	element_init_Zr(rdash,pairing);
	element_init_G1(temp_res,pairing);
	element_random(rdash);
	element_init_Zr(beta_j,pairing);
	element_init_G1(ct.A,pairing);
	element_init_G1(temp_mult,pairing);
	element_init_G1(temp_mult1,pairing);
	element_init_G1(temp_pow1,pairing);
	element_init_G1(temp_pow2,pairing);
	element_init_G1(hi,pairing);
	element_random(hi);
	element_init_G1(fi,pairing);
	element_random(fi);
	element_init_G1(Ci,pairing);
	element_init_G1(Bj,pairing);
	element_pow_zn(ct.A, gp.g, r);
	element_printf("A = %B\n",ct.A);
	printf("----------------------\n");
	
	
	long int j;
	element_t tj,alpha_j;
	element_init_G1(tj,pairing);
	element_t sj;
	element_init_Zr(sj,pairing);
	element_init_Zr(alpha_j,pairing);
	//printf("***************OKAY****************\n");
	for(j=0;j<m;j++)
	{
		//element_printf("ID = %B\n",Rc.ID[j]);
		//element_printf("gp.gpub = %B\n",gp.gpub);
		//element_printf("Rc.pk[j].y = %B\n",Rc.pk[j].y);
		//element_printf("Rc.pk[j].t = %B\n",Rc.pk[j].t);
		h3(Rc.ID[j], gp.gpub, Rc.pk[j].y, Rc.pk[j].t, temp_res);
		//element_printf("Temp Res = %B\n",temp_res);
		element_set(beta_j,temp_res);
		//element_printf("Beta = %B\n",beta_j);
		element_pow_zn(temp_pow1, Rc.pk[j].y, beta_j);
		element_mul(temp_mult, temp_pow1, Rc.pk[j].t);
		element_random(sj);
		element_pow_zn(tj, gp.g, sj);         
		h0(Rc.ID[j],tj,alpha_j);
		element_pow_zn(temp_pow2, gp.gpub , alpha_j);
		element_mul(temp_mult1, temp_mult, temp_pow2);
		element_pow_zn(Bj, temp_mult1 , rdash);
		element_init_G1(ct.B[j],pairing);
		element_set(ct.B[j],Bj);
		element_printf("B%d = %B\n",j,ct.B[j]);
	}
	printf("----------------------\n");
	long int i;
	for(i=0;i<n;i++)
	{
		H1(word[i], temp_res);
		element_pow_zn(hi, temp_res, r);
		H2(word[i], temp_res);
		element_pow_zn(fi, temp_res, rdash);
		element_mul(Ci, hi, fi);
		element_init_G1(ct.C[i],pairing);
		element_set(ct.C[i],Ci);
		element_printf("C%d = %B\n",i,ct.C[i]);
	}
	printf("----------------------\n");
}

void trapdoor(int j, Q* q){
	
	element_t betaj, t, prod; // define all variables
	
	// init variables
	element_init_Zr(betaj, pairing);
	element_init_Zr(t, pairing);
	element_init_G1(q->Tj1, pairing);
	element_init_G1(q->Tj2, pairing);
	element_init_G1(q->Tj3, pairing);
	element_init_G1(prod, pairing);
	
	h3(Rc.ID[j], gp.gpub, Rc.pk[j].y, Rc.pk[j].t, betaj); // calulate betaj using h3
	
	element_random(t); // set random t
	element_printf("t = %B\n",t);
	printf("----------------------\n");
	element_pow_zn(q->Tj1, gp.g, t); // calculate Tj1 = g^t
	
	// get H1 of all words
	H1(q->words[0], prod);
	element_t hash1;
	element_init_G1(hash1, pairing);
	for(int i=1;i<q->n_words;i++){
		H1(q->words[i], hash1);
		element_mul(prod, prod, hash1); // multiply all H1(words)
	}

	element_pow_zn(q->Tj2, prod, t); // Tj2 = product ^ t

	// get H2 of all words
	H2(q->words[0], prod);
	for(int i=1;i<q->n_words;i++){
		H2(q->words[i], hash1);
		element_mul(prod, prod, hash1); // multiply H2 of all words
	}
	element_t power; // power for Tj3
	element_init_Zr(power, pairing);
	
	element_mul(power, betaj, Rc.sk[j].x); // betej x Xj
	
	element_add(power, power, Rc.sk[j].d); // (betaj x Xj) + dj
	
	element_div(power, t, power); // t / ((betaj x Xj) + dj)
	
	element_pow_zn(q->Tj3, prod, power); // Tj3 = product(H2) ^ power
	
	//print all Tjs
	// element_printf("q->Tj1 = %B\n",q->Tj1);
	// printf("----------------------\n");
	// element_printf("q->Tj2 = %B\n",q->Tj2);
	// printf("----------------------\n");
	// element_printf("q->Tj3 = %B\n",q->Tj3);
	// printf("----------------------\n");

}

int Test(Q* q,int j)
{
	element_t lhs,rhs1,rhs2,rhs,product;
	element_init_GT(lhs,pairing);
	element_init_GT(rhs1,pairing);
	element_init_GT(rhs2,pairing);
	element_init_GT(rhs,pairing);
	element_init_G1(product,pairing);
	element_set(product,ct.C[0]);
	//element_printf("Product = %B\n",product);
	long int i;
	for(i=1;i<n;i++)
	{
		element_mul(product, product, ct.C[i]);
	}
	//element_printf("Product = %B\n",product);
	element_pairing(lhs,q->Tj1,product);
	//element_printf("LHS = %B\n",lhs);
	element_pairing(rhs1,ct.A,q->Tj2);
	element_pairing(rhs2,ct.B[j],q->Tj3);
	element_mul(rhs, rhs1, rhs2);
	return element_cmp(lhs, rhs);
}


int main(int argv,char **argc)
{
	char param[1024];
	size_t count = fread(param, 1, 1024, stdin);
	if (!count)
	{
		pbc_die("input error");
	}
	OpenSSL_add_all_algorithms();
	pairing_init_set_buf(pairing, param, count);
	
	//setup
	element_t temp_setup;
	element_init_Zr(temp_setup,pairing);
	element_random(temp_setup);
	element_init_G1(gp.g, pairing);
	element_random(gp.g);
	element_printf("g = %B\n",gp.g);
	printf("---------------------\n");
	Setup(temp_setup);
	
	//write code
	
	//test Extract_Partial_Private_Key
	element_t s;
	element_init_Zr(s,pairing);
	element_random(s);
	long int i;
	for(i=0;i<m;i++)
	{
		element_init_Zr(Rc.ID[i],pairing);
		element_random(Rc.ID[i]);
	}
	Extract_Partial_Private_Key(s);
	
	//test Set_Secret_Value
	printf("----------------------\n");
	Set_Secret_Value();
	
	//test Set_Public_Key
	printf("----------------------\n");
	Set_Public_Key();
	printf("----------------------\n");

	//test Hash
	element_t hashed_h0, hashed_H1, hashed_H2, hashed_h3;
	element_t A,B,C;
	element_init_G1(A, pairing);
	element_init_G1(B, pairing);
	element_init_GT(C, pairing);
	element_init_Zr(hashed_h0 ,pairing);
	element_init_G1(hashed_H1, pairing);
	element_init_G1(hashed_H2, pairing);
	element_init_Zr(hashed_h3, pairing);
	element_random(A);
	element_random(B);
	element_random(C);

	printf("Demo of h0\n");
	h0(Rc.ID[0], A, hashed_h0);
	element_printf("Ip 1 for h0 = %B\n",Rc.ID[0]);
	element_printf("Ip 2 for h0 = %B\n",A);
	element_printf("h0 output = %B\n",hashed_h0);
	printf("----------------------\n");

	char keyword[20] = "mockKeyword";

	printf("Demo of H1\n");
	H1(keyword, hashed_H1);
	element_printf("\nkeyword = %s\nhashed_H1 = %B\n", keyword, hashed_H1);
	printf("----------------------\n");

	printf("Demo of H2\n");
	H2(keyword, hashed_H2);
	element_printf("\nkeyword = %s\nhashed_H2 = %B\n", keyword, hashed_H2);
	printf("----------------------\n");

	printf("Demo of h3\n");
	h3(Rc.ID[0], A, B, C, hashed_h3);
	element_printf("Ip 1 for h3 = %B\n",Rc.ID[0]);
	element_printf("Ip 2 for h3 = %B\n",A);
	element_printf("Ip 3 for h3 = %B\n",B);
	element_printf("Ip 4 for h3 = %B\n",C);
	element_printf("hashed_h3 = %B\n", hashed_h3);
	printf("----------------------\n");
	printf("----------------------\n");
	
	char *words[n];
	words[0] = "keyword_1";
	words[1] = "keyword_2";
	words[2] = "keyword_3";
	mCLPECK(words);
	
	
	Q q_1;
	q_1.n_words = 1;
	q_1.words[0] = "mock";
	printf("======================\n");
	printf("Subset 1\n"); 
	printf("mock\n");
	trapdoor(0, &q_1); 	// call trapdoor with given subsets of keywords
	int result=0;
	result = Test(&q_1, i);
	printf("Result for subset %d = %d\n",0,result); 	// print result 
	printf("======================\n");

	Q q_2;
	q_2.n_words = 1;
	q_2.words[0] = "eval";
	printf("======================\n");
	printf("Subset 2\n"); 
	printf("eval\n");
	trapdoor(0, &q_2); 	// call trapdoor with given subsets of keywords
	result = Test(&q_2, i);
	printf("Result for subset %d = %d\n",0,result); 	// print result 
	printf("======================\n");


	Q q_3;
	q_3.n_words = 2;
	q_3.words[0] = "mock";
	q_3.words[1] = "eval";
	printf("======================\n");
	printf("Subset 3\n"); 
	printf("mock\n");
	printf("eval\n");
	trapdoor(0, &q_3); 	// call trapdoor with given subsets of keywords
	result = Test(&q_3, i);
	printf("Result for subset %d = %d\n",0,result); 	// print result 
	printf("======================\n");


	Q q_4;
	q_4.n_words = 2;
	q_4.words[0] = "eval";
	q_4.words[1] = "test";
	printf("======================\n");
	printf("Subset 4\n"); 
	printf("eval\n");
	printf("test\n");
	trapdoor(0, &q_4); 	// call trapdoor with given subsets of keywords
	result = Test(&q_4, i);
	printf("Result for subset %d = %d\n",0,result); 	// print result 
	printf("======================\n");

	Q q_5;
	q_5.n_words = 3;
	q_5.words[0] = "mock";
	q_5.words[1] = "eval";
	q_5.words[2] = "test";
	printf("======================\n");
	printf("Subset 5\n"); 
	printf("mock\n");
	printf("eval\n");
	printf("test\n");
	trapdoor(0, &q_5); 	// call trapdoor with given subsets of keywords
	result = Test(&q_5, i);
	printf("Result for subset %d = %d\n",0,result); 	// print result 
	printf("======================\n");


	// //for each receiver
	// for(int i=0;i<m;i++)
	// {
	// 	//set all words
	// 	for(int j=0;j<number_of_words;j++){
	// 		element_init_Zr(Q_rec[i].I[j], pairing);
	// 		element_set_si(Q_rec[i].I[j], j+1);

	// 		Q_rec[i].words[j] = "word";
	// 		// element_printf("I = %B\tword = %s\n", Q_rec[i].I[j], Q_rec[i].words[j]);
	// 	}
	// 	printf("----------------------\n");
	// 	trapdoor(0, &Q_rec[i]); 	// call trapdoor for ith receiver
	// 	int result=0;
	// 	result = Test(&Q_rec[i], i); 	// test for ith receiver
    // 	printf("======================\n");
    // 	printf("Result for Receiver %d = %d\n",i+1,result); 	// print result 
	// 	printf("======================\n");
	// }

	pairing_clear(pairing); // clear pairing

	return 0;
}

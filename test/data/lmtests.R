# R code for tests 
# $Id$

rm(list=ls())

des<-matrix(c(rep(1,9), rep(0,4), rep(1,5), 0.12,0.24,0.48,0.96,0.12,0.24,0.48,0.96,0.96), 9,3)
dat <- read.table("example.madata.small.txt",header=T,comment.char='',fill=T,sep='\t', as.is=T, row.names=1)
saminfo <- read.table("example.metadata.small.txt", row.names=1, header=T, sep='\t',comment.char='')
saminfo<-cbind(saminfo, c(1,1,2,2,1,1,2,2,2))
saminfo<-cbind(saminfo, c(  0.12,0.24,0.48,0.96,0.12,0.24,0.48,0.96,0.96 ))
colnames(saminfo)<-c("Treat", "Batch", "Geno", "Value")
colnames(des)<-c("Intercept", "Type", "Amount")


V<-factor(saminfo$Treat)
B<-factor(saminfo$Batch)
G<-factor(saminfo$Geno)

# t-test
object<- lm(t(dat[1,]) ~  + factor(des[,"Type"]))
summary(object)
object<- lm(t(dat[11,]) ~  + factor(des[,"Type"]))
summary(object)

# two-way ANOVA, no interaction
object<- lm(t(dat[1,]) ~  + factor(des[,"Type"]) + des[,"Amount"] + 1 )
summary(object)
anova(object)
object<- lm(t(dat[11,]) ~  + factor(des[,"Type"]) + des[,"Amount"] + 1 )
summary(object)
 
# two factors + interaction
object<-lm(t(dat[1,]) ~V + G + V*G)
summary(object)
anova(object)
object<-lm(t(dat[11,]) ~V + G + V*G)
summary(object)
anova(object)


# testLSFTwoLevelsOneContinuous
# two factors with covariate
object<-lm(t(dat[1,]) ~V + saminfo$Value)
summary(object)
anova(object)
object<-lm(t(dat[11,]) ~V + saminfo$Value)
summary(object)
anova(object)

# 3 factors including covariate
object<-lm(t(dat[1,]) ~V + G + saminfo$Value)
summary(object)
anova(object)
object<-lm(t(dat[11,]) ~ V + G + saminfo$Value)
summary(object)
anova(object)

# testTwoWayTwoLevelsOneContinousInteractionC
model.matrix(t(dat[1,]) ~V + G + V*G + saminfo$Value)
object<-lm(t(dat[1,]) ~V + G + V*G + saminfo$Value)
summary(object)
anova(object)
object<-lm(t(dat[11,]) ~ V + G + V*G + saminfo$Value)
summary(object)
anova(object)


# with missing values
datm<-read.delim("example.madata.withmissing.small.txt", row.names=1  )

# testLSFOneContinuousWithMissing3#
object<-lm(t(datm[1,]) ~ saminfo$Value) 
summary(object)
object$fitted
anova(object)
object<-lm(t(datm[11,]) ~ saminfo$Value)
summary(object)
anova(object)
object<-lm(t(datm["228980_at",]) ~ saminfo$Value)
summary(object)
object$fitted
anova(object)
object<-lm(t(datm["214502_at",]) ~ saminfo$Value)
summary(object)
anova(object)
object$fitted
object<-lm(t(datm["232018_at",]) ~ saminfo$Value)
summary(object)
anova(object)
object$fitted
object<-lm(t(datm["1553129_at",]) ~ saminfo$Value)
summary(object)
anova(object)
object$fitted
 object<-lm(t(datm[1,]) ~ V) 
summary(object)
anova(object)
object$fitted
anova(object)



################## 
# the following are tests used in Gemma as well.

factor1<-factor(c("a","a","a","a","b_base","b_base","b_base","b_base"));
factor2<-factor(c("c","c","d_base","d_base","c","c","d_base","d_base"));
factor3<-factor(c("u","v","w_base", "u","v","w_base","u","v"))

contrasts(factor1)<-contr.treatment(levels(factor1), base=2)
contrasts(factor2)<-contr.treatment(levels(factor2), base=2)
contrasts(factor3)<-contr.treatment(levels(factor3), base=3)

factor1<-relevel(factor1, "b_base")


dat<-read.table("anova-test-data.txt", header=T,row.names=1, sep='\t')
osttdat<-read.table("onesample-ttest-data.txt", header=T, row.names=1, sep='\t')

dm<-data.frame(factor1,factor2)

des<-data.frame(factor1,factor2,factor3)
row.names(des)<-colnames(dat)
#write.table(des, file="anova-test-des.txt", quote=F, sep='\t')

# basic anova
ancova<-apply(dat, 1, function(x){lm(x ~ factor1+factor2 )})
summary(ancova$probe_4)
summary(ancova$probe_10)
summary(ancova$probe_98)
anova(ancova$probe_4)
anova(ancova$probe_10)
anova(ancova$probe_98)
#etc

model.matrix(t(dat[1,]) ~ factor3)
model.matrix(t(dat[1,]) ~factor1 * factor3 )

object<-lm(t(dat[1,]) ~factor1 * factor3)

# ancova with continuous covariate
v<-c(1,2,3,4,5,6,7,8)
ancova2<-apply(dat, 1, function(x){ lm(x ~ factor1+factor2+v)})
model.matrix(t(dat[1,]) ~factor1+factor2+v)

summary(ancova2$probe_4)
summary(ancova2$probe_10)
summary(ancova2$probe_98)
anova(ancova2$probe_4)
anova(ancova2$probe_10)
anova(ancova2$probe_98)


# one way anova
owanova<-apply(dat, 1,  function(x){anova(lm(x ~ factor3))});
owanova$probe_4
owanova$probe_10
owanova$probe_98
summary(lm(t(dat["probe_4",]) ~ factor3))
summary(lm(t(dat["probe_10",]) ~ factor3))
object<-lm(t(dat["probe_98",]) ~ factor3)
model.matrix(t(dat["probe_4",]) ~ factor3)
attributes(object)
object$assign
object$effects
summary(object)
object<-lm(t(dat["probe_60",]) ~ factor3)
object<-lm(t(dat["probe_21",]) ~ factor3)
anova(object)


# one sample t-test
osttest<-apply(osttdat, 1, function(x){lm(x ~ 1)})
osttest<-rowTtest(osttdat);
summary(osttest$probe_4)
summary(osttest$probe_10)
summary(osttest$probe_16)
summary(osttest$probe_17)
summary(osttest$probe_98)
anova(osttest$probe_4)
anova(osttest$probe_10)
anova(osttest$probe_16)
anova(osttest$probe_17)
anova(osttest$probe_98)

#etc

# anova without interactions
anovaA<-apply(dat, 1, function(x){lm(x ~ factor1+factor2)})
summary(anovaA$probe_4)
summary(anovaA$probe_10)
summary(anovaA$probe_98)
anova(anovaA$probe_4)
anova(anovaA$probe_10)
anova(anovaA$probe_98)
# etc

# anova with interactions
anovaB<-apply(dat, 1, function(x){lm(x ~ factor1*factor2)})
summary(anovaB$probe_4)
summary(anovaB$probe_10)
summary(anovaB$probe_98)
anova(anovaB$probe_4)
anova(anovaB$probe_10)
anova(anovaB$probe_98)

# anova with more than 2 levels in one factor
contrasts(factor3)<-contr.treatment(levels(factor3), base=3)
anovaD<-apply(dat, 1, function(x){lm(x ~ factor1+factor3)})
summary(anovaD$probe_4)
summary(anovaD$probe_10)
summary(anovaD$probe_98)  


# two-sample ttest
ttestd<-apply(dat, 1, function(x){try (lm(x ~ factor1), silent=T)})
summary(ttestd$probe_0)
summary(ttestd$probe_4)
summary(ttestd$probe_10)
summary(ttestd$probe_98)
anova(ttestd$probe_0)
anova(ttestd$probe_4)
anova(ttestd$probe_10)
anova(ttestd$probe_98)

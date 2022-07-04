# R code for tests 

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

# testLSFTwoLevels
a<-lm(t(dat["1553129_at",]) ~ saminfo$Visit )
effects(a)
summary(a)
anova(a)
a<-lm(t(dat["232018_at",]) ~ saminfo$Visit )
effects(a)
# t-test
object<- lm(t(dat[1,]) ~  + factor(des[,"Type"]))
summary(object)
chol2inv(object$qr$qr, size=object$qr$rank)

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
rstudent(object)
object<-lm(t(dat[11,]) ~V + saminfo$Value)
summary(object)
anova(object)
rstudent(object)

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

# For weighted version:
library(limma)
datm<-read.delim("example.madata.withmissing.small.txt", row.names=1  )
voom(datm, model.matrix(saminfo)  ,lib.size=colSums(d))



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

# what we do is more like (for probe_60)
lmFit(dat, model.matrix(~ factor3))
o2$coefficients[58,,drop=F]


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

dm1027<-as.matrix(read.table("1027_GSE6189.designmatrix.txt", header=T, row.names=1))
qr(dm1027)
qr.R(qr(dm1027))
qr.Q(qr(dm1027))
da1027<-as.matrix(read.table("1027_GSE6189.data.test.txt", header=T, row.names=1)[1,])

dim(da1027)
des1027<- read.table("1027_GSE6189_expdesign.data.txt", header=T, row.names=1) 
dim(des1027)
object<-lm( da1027[1,]   ~  des1027$Treatment  +  des1027$SamplingTimePoint  +  des1027$batch) 
mm<-model.matrix( da1027[1,]   ~ des1027$Treatment  +  des1027$SamplingTimePoint  +  des1027$batch)
effects(object)
object$effects
summary(object)
anova(object)
rstudent(object)

model.matrix.default( da1027[1,]   ~ des1027$Treatment  +  des1027$SamplingTimePoint  +  des1027$batch)

form<-da1027[1,]   ~ des1027$Treatment  +  des1027$SamplingTimePoint  +  des1027$batch
d <- model.frame( form, data=environment(form), xlev=NULL)
namD <- names(d)
for(i in namD)
	if(is.character(d[[i]])) {
		d[[i]] <- factor(d[[i]])
		warning(gettextf("variable '%s' converted to a factor", i),
				domain = NA)
	}
contr.funs <- as.character(getOption("contrasts"))
isF <- sapply(d, function(x) is.factor(x) || is.logical(x) )
isOF <- sapply(d, is.ordered)
for(nn in namD[isF])            # drop response
	if(is.null(attr(d[[nn]], "contrasts")))
		contrasts(d[[nn]]) <- contr.funs[1 + isOF[nn]]

t <-terms(object, data=d)
.Internal(model.matrix(t, d))
# model.matrix is defined in model.c::attribute_hidden do_modelmatrix


y<-da1027[1,]
x<-mm
#x<-dm1027
storage.mode(x) <- "double"
storage.mode(y) <- "double"
p <- ncol(x)
n <- nrow(x)
ny <- NCOL(y)
tol<-1e-7
z <- .Fortran("dqrls",
		qr = x, n = n, p = p,
		y = y, ny = ny,
		tol = as.double(tol),
		coefficients = mat.or.vec(p, ny),
		residuals = y, effects = y, rank = integer(1L),
		pivot = 1L:p, qraux = double(p), work = double(2*p),
		PACKAGE="base")
z$effects
z$coefficients
z$pivot

### Yet another another example
dat<-read.table("1064_GSE7863.data.test.txt", header=T, row.names=1)
des<-read.table("1064_GSE7863_expdesign.data.test.txt", header=T, row.names=1)
model.matrix(t(dat[1,]) ~  des$Genotype  + des$OrganismPart  + des$Treatment  )
levels(des$Treatment)
object<-lm(t(dat["1415696_at",]) ~ des$Genotype  + des$OrganismPart  + des$Treatment  )
object<-lm(t(dat["1415696_at",]) ~ des$Genotype + des$Treatment  + des$OrganismPart   )

object<-lm(t(dat["1415837_at",]) ~ des$Genotype  + des$OrganismPart  + des$Treatment  )
object<-lm(t(dat["1416179_a_at",]) ~ des$Genotype  + des$OrganismPart  + des$Treatment  )
object<-lm(t(dat["1456759_at",]) ~ des$Genotype  + des$OrganismPart  + des$Treatment  )

summary(object)
anova(object)

# LeastSquaresFitTest.testVectorWeightedRegress
x<-c(1, 2, 3, 4, 5, 6, 7, 8, 9, 10 );
y<-c(1, 2, 2, 3, 3, 4, 4, 5, 5, 6);
summary(lm(y ~ x))
residuals(lm(y ~ x ))
w<-1/x;
summary(lm(y ~ x , weights = w))
residuals(lm(y ~ x , weights = w))
weights(lm(y ~ x , weights = w))
fitted(lm(y ~ x, weights = w ))
mm<-model.matrix(y ~ x)
 lm.wfit(mm, y, w =w) 
lm.fit(mm*sqrt(w), y*sqrt(w))

# LeastSquaresFitTest.testMatrixWeightedRegress
x<-matrix(nrow=2, ncol=5)
x[1,]<-c(1, 2, 3, 4, 5)
x[2,]<-c(1, 1, 6, 3, 2)
design<-matrix(nrow=3, ncol=5)
design[1,]<-1
design[2,]<-c(1, 2, 2, 3, 3)
design[3,]<-c(2, 1, 5, 3, 4)
design<-t(design)
w<-x
fit<-lm.wfit(design, x[1,], w=w[1,])
coefficients(fit)
residuals(fit)
fit$fitted.values
fit<-lm.wfit(design, x[2,], w=w[2,])
coefficients(fit)
residuals(fit)
fit$fitted.values


##################################################
# TestMathUtil.testApprox()
k<-approxfun(x=c(9:0),y=1/c(1:10)^2, rule=2)
k(c(9:0)+0.5)

# MeanVarianceEstimatorTest.testColSumsWithMissing
x<-matrix(nrow=2, ncol=3)
x[1,]<-c(1, 2, NA)
x[2,]<-c(4, 5, 6)
colSums(x, na.rm=T)

# MeanVarianceEstimatorTest.testCountsPerMillionWithMissing
x<-matrix(nrow=2, ncol=3)
x[1,]<-c(1, 2, NA)
x[2,]<-c(4, 5, 6)
lib.size<-colSums(x, na.rm=T)
t(log2(t(x+0.5)/(lib.size+1)*1e6))

# MeanVarianceEstimatorTest.testGetWeights
x<-read.table(header=T,row.names=1,'lmtest11.dat.txt')
y<-read.table(header=T,row.names=1,'lmtest11.des.txt')
design<-model.matrix( ~ y$targets.TreatmentDHT )
w<-voom(x,design)
w$weights[1,]
w$weights[100,]
w$weights[150,]

# QRDecompositionTest.testScaledDesign
data <- read.csv('lmtest2.dat.txt',header=T,row.names=1,sep='\t')
design<-read.table('lmtest3.des.txt',row.names=1,header=T)
q<-qr(design*2)
q$qr[1:5,1:5]

# MeanVarianceEstimatorTest.testDuplicateRows
x<-read.table(header=T,row.names=1,'lmtest11.dat.txt')
x[2,]<-x[1,]
y<-read.table(header=T,row.names=1,'lmtest11.des.txt')
design<-model.matrix( ~ y$targets.TreatmentDHT )
w<-voom(x,design)
w$weights[1,]
w$weights[2,]
w$weights[150,]

# MeanVarianceEstimatorTest.testMeanVarianceNoDesignWithMissing
cpm <- function(x) {
    lib.size <- colSums(x, na.rm=T)
    t(log2(t(x+0.5)/(lib.size+1)*1e6))
}
stdVar <- function(x) { # R's var() is a bit different, probably from stdev^2
     sum((x-mean(x,na.rm=T))^2/(length(which(!is.na(x)))),na.rm=T)
}
x<-read.table('example.madata.withmissing.small.txt', row.names=1, header=T, sep='\t')
x<-cpm(x)
d<-data.frame(x=(rowMeans(x,na.rm=T)), y=(apply(x,1,stdVar)))
l<-lowess(x=d$x, y=d$y, f=0.5, iter=3) # lowess doesn't handle missing values
plot(x=d$x, y=d$y, pch=21, bg='black')
lines(x=l$x, y=l$y, col='red')


# limma tests
library(limma)
options(digits=10)
datam<-read.delim("limmatest.data.txt", row.names=1)
designm<-read.delim("limmatest.design.txt", row.names=1)
f<-lmFit(datam, design=model.frame(designm))
f<-eBayes(f)
# effects
effects<-t(qr.qty(f$qr,t(as.matrix(datam))))
write.table(effects, "limmatest.fit.effect.txt", sep='\t', quote=F)
# test of using data that has missing values
datam.missing<-as.matrix(datam)
#randomly add some missing data
set.seed(1)
datam.missing[sample.int(600, 50)]<-NA
f2<-lmFit(datam.missing, design=model.frame(designm))
f2<-eBayes(f2)
write.table(datam.missing, file="limmatest.data.missing.txt", sep='\t', quote=F)
write.table(f2$s2.post, "limmatest.fit.squeezevar.missing.txt", sep='\t', quote=F)
#qr2<-qr(f2)
# due to missing values we can't compute effects (easily)
#effects2<-t(qr.qty(qr(f2),t(as.matrix(datam.missing)))) # nope
# also nope:
#.Fortran(.F_dqrqty, as.double(qr2$qr), nrow(qr2$qr), qr2$rank, as.double(qr2$qraux), 
#         as.matrix(datam), NCOL(as.matrix(datam)), qty = as.matrix(datam))$qty
# effects2 <- t(qr.Q(qr(f2))) %*% as.matrix(datam.missing)
#write.table(effects2, "limmatest.fit.effect.txt", sep='\t', quote=F)
f2$coefficients[4,]


